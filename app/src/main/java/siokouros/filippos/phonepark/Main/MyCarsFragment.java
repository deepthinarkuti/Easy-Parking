package siokouros.filippos.phonepark.Main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import siokouros.filippos.phonepark.Model.Car;
import siokouros.filippos.phonepark.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyCarsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyCarsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCarsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button registerCar;
    Button addCarButton;
    ArrayList<Car> cars = new ArrayList<Car>();

    FragmentManager manager;


    TextView carsTextView;


    //FirebaseShit
    //firebase Shit
    FirebaseAuth auth;
    String userID;
    DatabaseReference carsDatabaseReference;
    FirebaseDatabase firebaseDatabase;


    public MyCarsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyCarsFragment.
     */
    public static MyCarsFragment newInstance(String param1, String param2) {
        MyCarsFragment fragment = new MyCarsFragment();
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_cars, container, false);
        manager = getFragmentManager();
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.cars_recycler_view);


        auth = auth = FirebaseAuth.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        carsDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("Cars");
        carsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Car car = new Car();
                    car.setRegNumber(ds.child("regNumber").getValue().toString());
                    car.setColour(ds.child("colour").getValue().toString());
                    car.setMake(ds.child("make").getValue().toString());
                    car.setModel(ds.child("model").getValue().toString());
                    car.setYear(ds.child("year").getValue().toString());
                    cars.add(car);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"There are no cars.", Toast.LENGTH_SHORT).show();
            }
        });

    /*    StringBuilder builder = new StringBuilder();
        for (Car details : cars) {
            builder.append(details + "\n");
            carsTextView.setText(builder.toString());
        }*/


        addCarButton = rootView.findViewById(R.id.add_car_button_my_cars_fragment);
        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterCarFragment registerCarFragment = new RegisterCarFragment();
                manager.beginTransaction()
                        .replace(R.id.main_frame_fragment, registerCarFragment).addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
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
