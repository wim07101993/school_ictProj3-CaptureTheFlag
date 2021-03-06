package comwim07101993ictproj3_capturetheflag.github.caperevexillum.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import comwim07101993ictproj3_capturetheflag.github.caperevexillum.R;
import comwim07101993ictproj3_capturetheflag.github.caperevexillum.activities.GameActivity;

/**
 * Gives an fragment where you can choose to begin the quiz or not
 * this is connected to the statemanager
 */
public class StartQuizFragment extends Fragment implements View.OnClickListener {

    /* ---------------------------------------------------------- */
    /* ------------------------- FIELDS ------------------------- */
    /* ---------------------------------------------------------- */
    View view;
    // List for listeners for changes
    private OnFragmentInteractionListener mListener;

    // Instance buttons
    private Button YesButton;
    private Button NoButton;

    private GameActivity gameActivity;


    /* --------------------------------------------------------------- */
    /* ------------------------- CONSTRUCTOR ------------------------- */
    /* --------------------------------------------------------------- */

    public StartQuizFragment() {
        // Required empty public constructor
    }

    /* ----------------------------------------------------------- */
    /* ------------------------- METHODS ------------------------- */
    /* ----------------------------------------------------------- */


    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameActivity = (GameActivity) getActivity();
       // Vibrator v = (Vibrator) gameActivity.getSystemService(Context.VIBRATOR_SERVICE);
        //v.vibrate(1000);

        // declarate the statemanager


        // declarate the buttons on the fragment
        YesButton = (Button) getView().findViewById(R.id.YesButton);
        NoButton = (Button) getView().findViewById(R.id.NoButton);

        // set the listener for the buttons
        YesButton.setOnClickListener(this);
        NoButton.setOnClickListener(this);
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_start_quiz, container, false);
        return view;
    }

    /**
     * @param context
     */
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
     * @param view looks which button is clicked
     *             set the state on the statemanager
     */
    @Override
    public void onClick(View view) {
        if (view == YesButton) {
            //  stateManager.set(QUIZ_STARTER, true);
            ((GameActivity) getActivity()).showQuiz(true);
        } else if (view == NoButton) {
            //stateManager.set(QUIZ_STARTER, false);
        }
        Done();
    }

    public void Done() {
        this.onDetach();
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

    }
}
