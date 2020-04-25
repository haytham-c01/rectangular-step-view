package com.haytham.coder.rectangularstepview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.haytham.coder.stepview.RectangularStepView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        stepView3.onStepChangedListener = {
            previousStep, currentStep ->
            Toast.makeText(this, "$previousStep -> $currentStep", Toast.LENGTH_SHORT).show()
        }
    }



    fun incrementStep(view: View) {
        stepView1.incrementStep()
        stepView2.incrementStep()
        stepView3.incrementStep()
        stepView4.incrementStep()
        stepView5.incrementStep()

    }

    fun decrementStep(view: View) {
        stepView1.decrementStep()
        stepView2.decrementStep()
        stepView3.decrementStep()
        stepView4.decrementStep()
        stepView5.decrementStep()
    }
    fun gotoStep(view: View) {
        val stepNumber= stepNumberEditText.text.toString().toInt()
        stepView1.gotoStep(stepNumber)
        stepView2.gotoStep(stepNumber)
        stepView3.gotoStep(stepNumber)
        stepView4.gotoStep(stepNumber)
        stepView5.gotoStep(stepNumber)
    }
}
