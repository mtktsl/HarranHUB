package com.example.harranhub.GirisEkrani;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harranhub.AnaSayfa.AnaSayfa;
import com.example.harranhub.AnaSayfa.AnaSayfaArabirimi;
import com.example.harranhub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GirisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GirisFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GirisFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GirisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GirisFragment newInstance(String param1, String param2) {
        GirisFragment fragment = new GirisFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    View rootView;
    private TextView editTextEposta, editTextSifre; //Giri?? login inputlar??
    private ProgressBar progressBar; //Progressbar
    private SharedPreferences.Editor editor; //Shared Preferences Editcisi

    FirebaseAuth auth; //Firebase Authentication
    SharedPreferences sp; //Shared Preferences

//    GirisArabirimi kontrolcu;
    AnaSayfaArabirimi kontrolcu;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_giris, container, false);

//        kontrolcu = ((GirisEkrani)getContext());
        kontrolcu = ((AnaSayfa)getContext());

        auth=FirebaseAuth.getInstance();
        sp= getContext().getSharedPreferences("kullaniciVeri", Context.MODE_PRIVATE); //Kullan??c?? verileri i??in Shared Preference
        editor=sp.edit();

        FirebaseUser user=auth.getCurrentUser();

        if (user!=null) {
            //kontrolcu.yonlendir(GirisFragmentDirections.actionGirisFragmentToAnaSayfa());
        }

        editTextEposta=rootView.findViewById(R.id.eposta);
        editTextSifre=rootView.findViewById(R.id.sifre);
        progressBar=rootView.findViewById(R.id.progressBar);

        Button girisTusu = rootView.findViewById(R.id.giris);
        Button kayitTusu = rootView.findViewById(R.id.kayit);

        girisTusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                girisYap(v);
            }
        });

        kayitTusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kontrolcu.yonlendir(GirisFragmentDirections.actionGirisFragmentToKayitFragment());
            }
        });

        TextView sifremiUnuttum = (TextView) rootView.findViewById(R.id.sifremiUnuttum);
        sifremiUnuttum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kontrolcu.yonlendir(GirisFragmentDirections.actionGirisFragmentToSifremiUnuttum2());
            }
        });


        return rootView;
    }

    public void girisYap (View view) { //Giri?? yapma foknsiyonu

        String eposta=editTextEposta.getText().toString().trim(); //E-posta de??i??keni
        String sifre=editTextSifre.getText().toString().trim(); //??ifre de??i??keni

        if (eposta.isEmpty()) { //E??er e-posta girdisi bo?? ise hata ver
            editTextEposta.setError("Bu alan bo?? b??rak??lamaz!");
            editTextEposta.requestFocus();
            kontrolcu.veriKontrol(false, "");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(eposta).matches()) { //E??er e-posta, e-posta format??nda de??ilse hata ver
            editTextEposta.setError("L??tfen ge??erli bir e-posta girin!");
            editTextEposta.requestFocus();
            kontrolcu.veriKontrol(false, "");
            return;
        }

        if (sifre.isEmpty()) { //E??er ??ifre girdisi bo?? ise hata ver
            editTextSifre.setError("Bu alan bo?? b??rak??lamaz!");
            editTextSifre.requestFocus();
            kontrolcu.veriKontrol(false, "");
            return;
        }

        progressBar.setVisibility(View.VISIBLE); //E??er girdilerde bir sorun yoksa progressBar'?? g??r??n??r yap



        auth.signInWithEmailAndPassword(eposta, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() { //Firebase Authentication e-posta ve ??ifre ile giri?? metodu
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) { //Task tamamland??????nda ??al????acak metod

                try {
                    if (task.isSuccessful()) { //E??er task ba??ar??yla tamamlan??rsa ??al????
                        Toast.makeText(rootView.getContext(), "Ba??ar??yla giri?? yap??ld??!", Toast.LENGTH_SHORT).show();

                        kontrolcu.veriKontrol(true, eposta);
                    }
                    else { //E??er task ba??ar??s??z bir ??ekilde tamamlan??rsa ??al????

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) { //E??er kullan??c?? bilgileri ge??ersiz ise hata ver
                            Toast.makeText(rootView.getContext(), "Giri?? yap??lamad??! L??tfen bilgilerinizi kontrol edip tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                        }
                        else if (task.getException() instanceof FirebaseAuthInvalidUserException) { //E??er kullan??c?? bilgileri yanl???? ise hata ver
                            Toast.makeText(rootView.getContext(), "Giri?? yap??lamad??! L??tfen bilgilerinizi kontrol edip tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                        }
                        else if (task.getException() instanceof FirebaseTooManyRequestsException) { //E??er ??ok fazla yanl???? deneme olursa hata ver
                            Toast.makeText(rootView.getContext(), "??st ??ste ??ok fazla deneme yapt??n??z! L??tfen ??ifrenizi s??f??rlay??n veya daha sonra tekrar deneyiz.", Toast.LENGTH_SHORT).show();
                        }
                        kontrolcu.veriKontrol(false, "");
                    }
                }
                catch (Exception e)
                {
                    Log.e("Giris Frag ERROR:", e.getMessage());
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}