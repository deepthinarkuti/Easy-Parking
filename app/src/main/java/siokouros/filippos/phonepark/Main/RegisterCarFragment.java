package siokouros.filippos.phonepark.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import siokouros.filippos.phonepark.Model.Car;
import siokouros.filippos.phonepark.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterCarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterCarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterCarFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegisterCarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterCarFragment.
     */
    public static RegisterCarFragment newInstance(String param1, String param2) {
        RegisterCarFragment fragment = new RegisterCarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    EditText editTextMake, editTextModel, editTextYear, editTextRegistrationNumber;
    Spinner colourSpinner;
    Button submitRegistrationButton;
    ProgressBar progressBar;


    //Firebase stuff
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase database;
    String userID;
    DatabaseReference databaseCarReference;
    DatabaseReference databaseNumberOfCarsReference;


    String counter;
    int counterInt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register_car, container, false);

        //all times on view
        editTextMake = rootView.findViewById(R.id.register_a_car_make);
        editTextModel = rootView.findViewById(R.id.register_a_car_model);
        editTextYear = rootView.findViewById(R.id.register_a_car_year);
        editTextRegistrationNumber = rootView.findViewById(R.id.register_a_car_registration_number);
        colourSpinner = rootView.findViewById(R.id.colourSpinner);
        submitRegistrationButton = rootView.findViewById(R.id.register_car_button);
        progressBar = rootView.findViewById(R.id.progressBarRegisterCar);
        progressBar.setVisibility(View.GONE);


        List<String> colours = new ArrayList<String>();
        colours.add("Red");
        colours.add("Black");
        colours.add("Green");
        colours.add("Blue");
        colours.add("Grey");
        colours.add("Orange");
        colours.add("Yellow");
        colours.add("White");
        colours.add("Silver");

        ArrayAdapter<String> coloursAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, colours);
        coloursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colourSpinner.setAdapter(coloursAdapter);


        //Firebase stuff
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        FirebaseUser user = auth.getCurrentUser();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        databaseCarReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Cars");
        databaseNumberOfCarsReference = FirebaseDatabase.getInstance().getReference("Users");


        submitRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegistration();
            }
        });

        return rootView;
    }

    private void submitRegistration() {
        String make = editTextMake.getText().toString().trim();
        String model = editTextModel.getText().toString().trim();
        String colour = colourSpinner.getSelectedItem().toString();
        String yearString = editTextYear.getText().toString().trim();

        String registrationNumber = editTextRegistrationNumber.getText().toString().trim();
         registrationNumber = registrationNumber.replace("\\s+","");

        if (make.isEmpty()) {
            editTextMake.setError("Make should not be empty");
            editTextMake.requestFocus();
            return;
        }
        if (model.isEmpty()) {
            editTextModel.setError("Model should not be empty");
            editTextModel.requestFocus();
            return;
        }
        if (yearString.isEmpty()) {
            editTextYear.setError("Year should not be empty");
            editTextYear.requestFocus();
            return;
        }
        if (registrationNumber.isEmpty()) {
            editTextRegistrationNumber.setError("Registration number should not be empty");
            editTextRegistrationNumber.requestFocus();
            return;
        }
        if (registrationNumber.contains(" ")) {
            editTextRegistrationNumber.setError("Please detele space");
            editTextRegistrationNumber.requestFocus();
            return;
        }
        if (registrationNumber.contains(" ")) {
            editTextRegistrationNumber.setError("Please detele spaces");
            editTextRegistrationNumber.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
        Car car = new Car(make, model, yearString, colour, registrationNumber);

        databaseCarReference.child(car.getRegNumber()).setValue(car).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    //Set the value of cars per user to +1
                    updateCars();

                    Toast.makeText(getActivity(), "Car registered successful", Toast.LENGTH_LONG).show();

//                    ParkingFragment parkingFragment = new ParkingFragment();
//                    manager.beginTransaction()
//                            .replace(R.id.main_frame_fragment, parkingFragment)
//                            .commit();
                } else {
                    Toast.makeText(getActivity(), "Car not registered", Toast.LENGTH_LONG).show();

                }
            }
        });


    }

    private void updateCars() {
        databaseNumberOfCarsReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (userID.equals(ds.getKey())) {
                        counter = ds.child("numberOfCars").getValue().toString();
                        counterInt = Integer.parseInt(counter);
                        ds.child(userID).getRef().removeValue();
                        counterInt++;
                        databaseNumberOfCarsReference.child(userID).child("numberOfCars").setValue((String.valueOf(counterInt)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
