package siokouros.filippos.phonepark.StartUp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;

import siokouros.filippos.phonepark.Interfaces.PublicFunctions;
import siokouros.filippos.phonepark.Main.MainActivity;
import siokouros.filippos.phonepark.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogIn.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogIn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogIn extends Fragment implements PublicFunctions, View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    ProgressBar loginProgressbar;
    Button loginButton;
    TextView loginSignupText;
    EditText loginEmailEditText;
    EditText loginPasswordEditText;

    FirebaseAuth auth;

    public LogIn() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogIn.
     */
    public static LogIn newInstance(String param1, String param2) {
        LogIn fragment = new LogIn();
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



    @Override
    public void onStart(){
        super.onStart();
        hideKeyboard(getActivity());
       }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_log_in, container, false);


        loginSignupText = rootView.findViewById(R.id.signUpTextView);
        loginProgressbar = rootView.findViewById(R.id.progressBarSignUp);
        loginProgressbar.setVisibility(View.GONE);



        auth = FirebaseAuth.getInstance();
        loginButton = rootView.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        loginEmailEditText = rootView.findViewById(R.id.loginEmailEditText);
        loginPasswordEditText =  rootView.findViewById(R.id.logInPasswordEditText);


        try{
            if(!getArguments().getString("loginEmail").isEmpty()){
                loginEmailEditText.setText(getArguments().getString("loginEmail"));

            }
        }catch(Exception ignored){
        }

        loginSignupText.setOnClickListener(this);



        return rootView;
       }





    public void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    private void userLogin(){


        String loginEmail = loginEmailEditText.getText().toString().trim();
        String loginPassword = loginPasswordEditText.getText().toString().trim();
        
        if(loginEmail.isEmpty()){
            loginEmailEditText.setError("Email is required");
            loginEmailEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()){
            loginEmailEditText.setError("Please enter a valid loginEmail");
            loginEmailEditText.requestFocus();
            return;
        }

        if(loginPassword.isEmpty()){
            loginPasswordEditText.setError("Password is required");
            loginPasswordEditText.requestFocus();
            return;
        }

        if(loginPassword.length()<6){
            loginPasswordEditText.setError("Minimum length of loginPassword should be 6");
            loginPasswordEditText.requestFocus();
            return;
        }

        loginProgressbar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loginProgressbar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"User signed in",Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
                    startActivity(myIntent);
                }else{
                    Toast.makeText(getActivity(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signUpTextView:
                SignUp signup = new SignUp();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragmentArea,signup).addToBackStack(null)
                        .commit();
                break;
            case R.id.loginButton:
                hideKeyboard(getActivity());
                 userLogin();
        }



    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
