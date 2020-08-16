package siokouros.filippos.phonepark.Main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import siokouros.filippos.phonepark.Manifest;
import siokouros.filippos.phonepark.Model.Parking;
import siokouros.filippos.phonepark.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    String hours;
    String mins;
    String regNumber;
    String parkingKey;
    Double latitude;
    Double longitude;

    final String[] test = new String[1];

    boolean doubleBackToExitPressedOnce = false;


    Button payNowButton;
    TextView amountTextView;

    private LocationManager locationManager;

    final Parking parking = new Parking();

    //firebase Shit
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    String userID;
    DatabaseReference parkingDatabaseReference;
    DatabaseReference parkingDatabaseReferenceExtend;
    boolean extend;

    FragmentManager manager;
    private String extendedMin;
    private String extendedHour;


    public PaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentFragment.
     */
    public static PaymentFragment newInstance(String param1, String param2) {
        PaymentFragment fragment = new PaymentFragment();
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

    String parkingLocation;

    @SuppressLint({"SetTextI18n", "MissingPermission"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment, container, false);

        manager = getFragmentManager();


        final Bundle bundle = getArguments();
        if (bundle != null) {
            String arr[] = bundle.getString("Car").split(" ", 2);
            regNumber = arr[0];   //the
            hours = bundle.getString("hours");
            mins = bundle.getString("mins");
            extend = Boolean.valueOf(bundle.getString("extend"));
        }


        auth = auth = FirebaseAuth.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        parkingDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        amountTextView = rootView.findViewById(R.id.amount_text_view);
        payNowButton = rootView.findViewById(R.id.pay_now_button);


        payNowButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                if (!extend) {
                    parking.setHours(hours);
                    parking.setMinutes(mins);
                    getLocation();
                    //TODO change location

                    parking.setCompleted("false");
                    parking.setRegNumber(regNumber);
                    @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    @SuppressLint("SimpleDateFormat") DateFormat dateFormatNoSlashes = new SimpleDateFormat("dd:MM:yyyy");
                    @SuppressLint("SimpleDateFormat") DateFormat timeFormat = new SimpleDateFormat("kk:mm:ss");
                    Date date = new Date();
                    parking.setDate(dateFormat.format(date));
                    parking.setStartTime(timeFormat.format(date));
                    long miliseconds = date.getTime();
                    miliseconds = miliseconds + (Integer.parseInt(hours) * 3600000) + (Integer.parseInt(mins) * 60000);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(miliseconds);
                    parking.setEndTimeMilis(String.valueOf(miliseconds));
                    parking.setAmount(amountTextView.getText().toString().trim());
                    parkingKey = regNumber + "_" + dateFormatNoSlashes.format(date) + "_" + timeFormat.format(date);
                    parkingDatabaseReference.child("Parkings").child(parkingKey).setValue(parking).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Parking Payed successful", Toast.LENGTH_SHORT).show();
                                Bundle bundle1 = new Bundle();
                                bundle1.putString("keyOfParking", parkingKey);
                                ParkingFragment parkingFragment = new ParkingFragment();
                                parkingFragment.setArguments(bundle1);
                                manager.beginTransaction()
                                        .replace(R.id.main_frame_fragment, parkingFragment)
                                        .commit();
                            } else
                                Toast.makeText(getActivity(), "Parking payed unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    extendedMin = mins;
                    extendedHour = hours;
                    parkingDatabaseReferenceExtend = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Parkings");
                    parkingDatabaseReferenceExtend.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            outerloop:
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (Objects.equals(ds.child("completed").getValue(String.class), "false")) {
                                    test[0] = ds.getKey().toString();
                                    String endMilis = ds.child("endTimeMilis").getValue().toString();
                                    Long endMilisLong = Long.parseLong(endMilis);
                                    endMilisLong = endMilisLong +  Long.parseLong(extendedHour) * 3600000 + Long.parseLong(extendedMin) * 60000;
                                    Map<String, Object> taskMap = new HashMap<String, Object>();
                                    taskMap.put("endTimeMilis", String.valueOf(endMilisLong));
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Parkings").child(test[0]).updateChildren(taskMap);
                                    Toast.makeText(getActivity(), "Parking Extended successful", Toast.LENGTH_SHORT).show();
                                    break outerloop;

                                }
                            }
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("keyOfParking", test[0]);
                            ParkingFragment parkingFragment = new ParkingFragment();
                            parkingFragment.setArguments(bundle1);
                            manager.beginTransaction()
                                    .replace(R.id.main_frame_fragment, parkingFragment)
                                    .commit();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });





                }
            }
        });

        amountTextView.setText("€" + computeAmount(hours, mins));


        return rootView;
    }

    private void updateLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    private void getLocation(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        locationManager.getBestProvider(criteria, true);
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        try {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } catch (Exception e) {
            latitude = 0.0;
            longitude = 0.0;
            e.printStackTrace();
        }
        List<Address> addresses;
        parking.setLocation("Location could not be found");
        if (latitude == 0.0 && longitude == 0.0) {
            // toast
        } else {
            try {
                addresses = gcd.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    //while(locTextView.getText().toString()=="Location") {
                    parkingLocation = addresses.get(0).getAddressLine(0);
                    parking.setLocation(addresses.get(0).getAddressLine(0));
                    // }
                }

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }


    private String computeAmount(String hours, String mins) {
        double h = Integer.parseInt(hours) * 60.0;
        double m = Integer.parseInt(mins);
        return new DecimalFormat("0.00").format((h + m) / 40.0);
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
