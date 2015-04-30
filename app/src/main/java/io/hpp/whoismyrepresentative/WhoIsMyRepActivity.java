package io.hpp.whoismyrepresentative;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A form that takes a state, zip, or name, and returns the corresponding
 *  list of Representatives or Senators.
 */
public class WhoIsMyRepActivity extends ActionBarActivity {

    /**
     * References to EditText fields
     */
    EditText zipCodeText, reps4nameText, reps4stateText, senators4nameText, senators4stateText;
    GetMyRep getMyRep; // instance of GetMyRep Class.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instance of GetMyRep
        getMyRep = new GetMyRep(this);

        setContentView(R.layout.activity_who_is_my_rep);

        // set EditText field and connect to editorActionListener
        zipCodeText = (EditText) findViewById(R.id.zip_code);
        zipCodeText.setOnEditorActionListener(editorActionListener);
        reps4nameText = (EditText) findViewById(R.id.last_name_rep);
        reps4nameText.setOnEditorActionListener(editorActionListener);
        reps4stateText = (EditText) findViewById(R.id.state4rep);
        reps4stateText.setOnEditorActionListener(editorActionListener);
        senators4nameText = (EditText) findViewById(R.id.last_name_senator);
        senators4nameText.setOnEditorActionListener(editorActionListener);
        senators4stateText = (EditText) findViewById(R.id.state4senator);
        senators4stateText.setOnEditorActionListener(editorActionListener);
    }

    /**
     * A listener for the editor fields. Fires the correct method GetMyRep
     *  corresponding to each editor field.
     */
    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId != android.view.inputmethod.EditorInfo.IME_ACTION_SEND) return handled;

            // check which editor field sent the action and fire the correct send method.
            if (v == zipCodeText) {
                getMyRep.byZipCode(v.getText().toString());
                handled = true;
            } else if (v == reps4nameText) {
                getMyRep.repsByName(v.getText().toString());
                handled = true;
            } else if (v == reps4stateText) {
                getMyRep.repsByState(v.getText().toString());
                handled = true;
            } else if (v == senators4nameText) {
                getMyRep.senatorsByName(v.getText().toString());
                handled = true;
            } else if (v == senators4stateText) {
                getMyRep.senatorsByState(v.getText().toString());
                handled = true;
            }
            return handled;
        }
    };

}
