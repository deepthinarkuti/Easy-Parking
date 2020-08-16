package siokouros.filippos.phonepark.Main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import siokouros.filippos.phonepark.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExtendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExtendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExtendFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private int hour;
    private int min;

    ImageButton addHourButton, addMinButtor, subHourButton, subMinButton;
    EditText hourEditText, minEditText;
    Button continueButtonExtend;


    FragmentManager manager;
    private String regNumber;



    public ExtendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExtendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExtendFragment newInstance(String param1, String param2) {
        ExtendFragment fragment = new ExtendFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_extend, container, false);

        manager = getFragmentManager();

        final Bundle bundle = getArguments();
        if (bundle != null) {
            String arr[] = bundle.getString("Car").split(" ", 2);
            regNumber = arr[0];   //the
            }


        addHourButton = rootView.findViewById(R.id.add_hour_button_extend);
        addMinButtor = rootView.findViewById(R.id.add_min_button_extend);
        subHourButton = rootView.findViewById(R.id.sub_hour_button_extend);
        subMinButton = rootView.findViewById(R.id.sub_min_button_extend);
        hourEditText = rootView.findViewById(R.id.hour_edit_text_extend);
        minEditText = rootView.findViewById(R.id.min_edit_text_extend);
        continueButtonExtend = rootView.findViewById(R.id.continue_button_extend);
        addHourButton.setOnClickListener(this);
        addMinButtor.setOnClickListener(this);
        subHourButton.setOnClickListener(this);
        subMinButton.setOnClickListener(this);
        continueButtonExtend.setOnClickListener(this);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_hour_button_extend:
                hour = Integer.parseInt((hourEditText.getText().toString().trim()));
                if (hour == 23) {
                    hourEditText.setText((String.valueOf(0)));
                    break;
                }
                hour++;
                hourEditText.setText((String.valueOf(hour)));
                break;
            case R.id.sub_hour_button_extend:
                hour = Integer.parseInt((hourEditText.getText().toString().trim()));
                if (hour == 0) {
                    hourEditText.setText((String.valueOf(23)));
                    break;
                }
                hour--;
                hourEditText.setText((String.valueOf(hour)));
                break;
            case R.id.add_min_button_extend:
                min = Integer.parseInt((minEditText.getText().toString().trim()));
                if (min == 55) {
                    minEditText.setText(("00"));
                    break;
                }
                min += 5;
                minEditText.setText((String.valueOf(min)));
                break;
            case R.id.sub_min_button_extend:
                min = Integer.parseInt((minEditText.getText().toString().trim()));
                if (min == 0) {
                    minEditText.setText((String.valueOf(55)));
                    break;
                }
                min -= 5;
                minEditText.setText((String.valueOf(min)));
                break;
            case R.id.continue_button_extend:
                if (Integer.parseInt(hourEditText.getText().toString().trim()) == 00 && Integer.parseInt(minEditText.getText().toString().trim()) == 0) {
                    minEditText.setError("Time can't be 0:00");
                    minEditText.requestFocus();
                    break;
                }
                PaymentFragment paymentFragment = new PaymentFragment();
                final Bundle bundle = new Bundle();
                bundle.putString("extend","true");
                bundle.putString("Car" , regNumber);
                bundle.putString("hours",hourEditText.getText().toString().trim());
                bundle.putString("mins", minEditText.getText().toString().trim());
                paymentFragment.setArguments(bundle);
                manager.beginTransaction()
                        .replace(R.id.main_frame_fragment, paymentFragment).addToBackStack(null)
                        .commit();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
