package com.example.giorgioschirano.calcolometabolismobasale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CalculatorFragment extends Fragment {

    TextInputEditText weightT;
    TextInputEditText heightT;
    TextInputEditText ageT;
    TextView result;
    Spinner genderSpinner;
    Spinner unitSpinner;
    CheckBox selectCaloric;
    RadioGroup lifestyle;
    TextView selectLifestyleText;
    TextView resultCaloric;

    ArrayAdapter<CharSequence> spinnerGendAdapter;
    int gender; //0=gender 1=female

    ArrayAdapter<CharSequence> spinnerUnitAdapter;
    int unit; //0=metrtical 1=imperial

    //multiplier index for calories per day
    float mult = 0;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calculator, null);


        weightT = v.findViewById(R.id.weightText);
        heightT = v.findViewById(R.id.heightText);
        ageT = v.findViewById(R.id.oldText);
        result = v.findViewById(R.id.resultText);
        genderSpinner = v.findViewById(R.id.gender_spinner);
        unitSpinner = v.findViewById(R.id.unit_spinner);
        selectCaloric = v.findViewById(R.id.knowNeeds);
        lifestyle = v.findViewById(R.id.radioGroupLife);
        selectLifestyleText = v.findViewById(R.id.selectLifeTextView);
        resultCaloric = v.findViewById(R.id.resultCaloricText);

        //unit spinner adapter
        spinnerUnitAdapter = ArrayAdapter.createFromResource(getContext(), R.array.unit_names, android.R.layout.simple_spinner_item);
        spinnerUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(spinnerUnitAdapter);
        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //fetch the unit position 0 or 1
                unit = unitSpinner.getSelectedItemPosition();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //the default one is the metric system
                unit = 0;

            }
        });

        //gender spinner adapter
        spinnerGendAdapter = ArrayAdapter.createFromResource(getContext(), R.array.gender_names, android.R.layout.simple_spinner_item);
        spinnerGendAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(spinnerGendAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //fetch the gender position 0 or 1
                gender = genderSpinner.getSelectedItemPosition();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //the default one is the male gender
                gender = 0;

            }
        });

        final MaterialButton calculate = v.findViewById(R.id.calculateButton);
        calculate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //requires the unit and the gender in order to perform the calculations
                calculate(gender, unit);
            }
        });

        /*
        If the checkbox is checked the radio button and the textView for the calories are VISIBLE
        else they remains GONE
         */
        selectCaloric.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            selectLifestyleText.setVisibility(View.VISIBLE);
                            lifestyle.setVisibility(View.VISIBLE);
                        } else {
                            selectLifestyleText.setVisibility(View.GONE);
                            lifestyle.setVisibility(View.GONE);
                            resultCaloric.setVisibility(View.GONE);
                        }

                    }
                }
        );


        lifestyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.lowRadio:
                        mult = 1.2f;
                        break;
                    case R.id.mediumRadio:
                        mult = 1.3f;
                        break;
                    case R.id.highRadio:
                        mult = 1.4f;
                        break;
                }
            }
        });

        return v;
    }

    private void calculate(int gender, int unit) {

        /*
        if all the required fields are filled the program controls the gender
        and chooses the correct algorithm
         */

        switch (unit) {

            //metric
            case 0:

                //controls if the fields are filled
                if (Objects.requireNonNull(weightT.getText()).toString().trim().length() > 0 &&
                        Objects.requireNonNull(heightT.getText()).toString().trim().length() > 0 &&
                        Objects.requireNonNull(ageT.getText()).toString().trim().length() > 0) {

                    //if they are filled it takes the values
                    float weight = Float.parseFloat(Objects.requireNonNull(weightT.getText()).toString());
                    int height = Integer.parseInt(Objects.requireNonNull(heightT.getText()).toString());
                    int age = Integer.parseInt(Objects.requireNonNull(ageT.getText()).toString());

                    switch (gender) {

                        //man
                        case 0:

                            //Mifflin and St Jeor revised equation based on Harris - Benedict ones (metric)
                            float basalMetM = (10 * weight) + (6.25f * height) - (5 * age) + 5;
                            result.setVisibility(View.VISIBLE);
                            result.setText(getString(R.string.bmr_is) + " " + basalMetM);

                            if (selectCaloric.isChecked()) {

                                resultCaloric.setVisibility(View.VISIBLE);
                                resultCaloric.setText(getString(R.string.you_need_cal) + basalMetM * mult + " " + "Kcal");
                            }

                            break;

                        //woman
                        case 1:
                            //Mifflin and St Jeor revised equation based on Harris - Benedict ones (metric)
                            float basalMetF = (10 * weight) + (6.25f * height) - (5 * age) - 161;
                            result.setVisibility(View.VISIBLE);
                            result.setText(getString(R.string.bmr_is) + " " + basalMetF);

                            if (selectCaloric.isChecked()) {

                                resultCaloric.setVisibility(View.VISIBLE);
                                resultCaloric.setText(getString(R.string.you_need_cal) + basalMetF * mult + " " + "Kcal");
                            }

                            break;

                    }

                    //clears the EditText
                    weightT.getText().clear();
                    heightT.getText().clear();
                    ageT.getText().clear();

                } else {

                    Toast.makeText(getContext(), getString(R.string.empty_fields), Toast.LENGTH_LONG).show();
                }

                break;

            //imperial
            case 1:

                //controls if the fields are filled
                if (Objects.requireNonNull(weightT.getText()).toString().trim().length() > 0 &&
                        Objects.requireNonNull(heightT.getText()).toString().trim().length() > 0 &&
                        Objects.requireNonNull(ageT.getText()).toString().trim().length() > 0) {

                    //if they are filled it takes the values
                    float weight = Float.parseFloat(Objects.requireNonNull(weightT.getText()).toString());
                    int height = Integer.parseInt(Objects.requireNonNull(heightT.getText()).toString());
                    int age = Integer.parseInt(Objects.requireNonNull(ageT.getText()).toString());

                    switch (gender) {

                        //man
                        case 0:

                            //Mifflin and St Jeor revised equation based on Harris - Benedict ones (imperial)
                            float basalMetM = (4.536f * weight) + (15.88f * height) - (5 * age) + 5;
                            result.setVisibility(View.VISIBLE);
                            result.setText(getString(R.string.bmr_is) + " " + basalMetM);

                            if (selectCaloric.isChecked()) {

                                resultCaloric.setVisibility(View.VISIBLE);
                                resultCaloric.setText(getString(R.string.you_need_cal) + basalMetM * mult + " " + "Kcal");
                            }

                            break;

                        //woman
                        case 1:
                            //Mifflin and St Jeor revised equation based on Harris - Benedict ones (imperial)
                            float basalMetF = (4.536f * weight) + (15.88f * height) - (5 * age) - 161;
                            result.setVisibility(View.VISIBLE);
                            result.setText(getString(R.string.bmr_is) + " " + basalMetF);

                            if (selectCaloric.isChecked()) {

                                resultCaloric.setVisibility(View.VISIBLE);
                                resultCaloric.setText(getString(R.string.you_need_cal) + basalMetF * mult + " " + "Kcal");
                            }

                            break;

                    }

                    //clears the EditText
                    weightT.getText().clear();
                    heightT.getText().clear();
                    ageT.getText().clear();

                } else {

                    Toast.makeText(getContext(), getString(R.string.empty_fields), Toast.LENGTH_LONG).show();
                }

                break;

        }


    }

}


