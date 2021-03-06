package com.example.chain.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.chain.R
import com.example.chain.data.Data
import com.example.chain.data.TodayBody
import com.example.chain.data.WeekBody
import com.github.kittinunf.fuel.Fuel
import com.github.mikephil.charting.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.fragment_goal.*
import kotlinx.android.synthetic.main.fragment_goal.view.*
import kotlinx.android.synthetic.main.goal_config_dialog.view.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


class GoalFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goal, container, false)

        pieChartToday(65f, view)
        pieChartWeek(45f, view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var h1 = 0
        var m1 = 0
        var h2 = 0
        var m2 = 0
        var h3 = 0
        var m3 = 0
        var h4 = 0
        var m4 = 0

        var todayGoal: Int = 0
        var weekGoal: Int = 0

        Fuel.get("http://192.168.32.157:8000/api/chainApi/")
            .responseString() { request, response, result ->
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val adapter = moshi.adapter(Data::class.java)
                val fromJson = adapter.fromJson(result.get())
                if (fromJson != null) {

                    h1 = fromJson.todayGoal/60
                    m1 = fromJson.todayGoal%60
                    h3 = fromJson.weekGoal/60
                    m3 = fromJson.weekGoal%60

                    setText(h1, h2, h3, h4, m1, m2, m3, m4)
                }
            }




        lateinit var alertDialog: AlertDialog

        val button1: Button = ChangeButton1
        button1.setOnClickListener {


            val inflater: LayoutInflater = this.layoutInflater
            val dialogView: View = inflater.inflate(R.layout.goal_config_dialog, null)
            // NumberPicker ??????
            val numberPicker1: NumberPicker = dialogView.numberPicker1
            val numberPicker2: NumberPicker = dialogView.numberPicker2
            val numberPicker3: NumberPicker = dialogView.numberPicker3
            val numberPicker4: NumberPicker = dialogView.numberPicker4

            //val dialogButton1: Button = DialogButton1

            // ????????????????????????
            numberPicker1.maxValue = 9
            numberPicker1.minValue = 0
            numberPicker2.maxValue = 9
            numberPicker2.minValue = 0
            numberPicker3.maxValue = 5
            numberPicker3.minValue = 0
            numberPicker4.maxValue = 9
            numberPicker4.minValue = 0

            //val num = numberPicker.value
            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            dialogBuilder.setView(dialogView)
                .setPositiveButton("??????", DialogInterface.OnClickListener { dialog, id ->
                    h1 = (numberPicker1.value * 10) + numberPicker2.value
                    m1 = (numberPicker3.value * 10) + numberPicker4.value
                    setText(h1, h2, h3, h4, m1, m2, m3, m4)
                    todayGoal = h1 * 60 + m1//???????????????????????????????????????
                    weekGoal = h3 * 60 + m3


                    //data class Body(val todayGoal:Int)

                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val requestAdapter = moshi.adapter(TodayBody::class.java)
                    val header: HashMap<String, String> =
                        hashMapOf("Content-Type" to "application/json")
                    val bodyJson = TodayBody(todayGoal = todayGoal, weekGoal = weekGoal)

                    Fuel.post("http://192.168.32.157:8000/api/chainApi/")
                        .header(header)
                        .body(requestAdapter.toJson(bodyJson))
                        .response { request, response, result ->
                            println(request)
                            println(response)
                            println(result.get())
                        }
                })

            alertDialog = dialogBuilder.create();
            //alertDialog.window!!.getAttributes().windowAnimations = R.style.PauseDialogAnimation
            alertDialog.show()

        }

        val button2: Button = ChangeButton2
        button2.setOnClickListener {

            val inflater: LayoutInflater = this.layoutInflater
            val dialogView: View = inflater.inflate(R.layout.goal_config_dialog, null)
            // NumberPicker ??????
            val numberPicker1: NumberPicker = dialogView.numberPicker1
            val numberPicker2: NumberPicker = dialogView.numberPicker2
            val numberPicker3: NumberPicker = dialogView.numberPicker3
            val numberPicker4: NumberPicker = dialogView.numberPicker4

            // ????????????????????????
            numberPicker1.maxValue = 9
            numberPicker1.minValue = 0
            numberPicker2.maxValue = 9
            numberPicker2.minValue = 0
            numberPicker3.maxValue = 5
            numberPicker3.minValue = 0
            numberPicker4.maxValue = 9
            numberPicker4.minValue = 0

            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            dialogBuilder.setView(dialogView)
                .setPositiveButton("??????", DialogInterface.OnClickListener { dialog, id ->
                    h3 = (numberPicker1.value * 10) + numberPicker2.value
                    m3 = (numberPicker3.value * 10) + numberPicker4.value
                    setText(h1, h2, h3, h4, m1, m2, m3, m4)
                    weekGoal = h3 * 60 + m3
                    todayGoal = h1 * 60 + m1

                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val requestAdapter = moshi.adapter(WeekBody::class.java)
                    val header: HashMap<String, String> =
                        hashMapOf("Content-Type" to "application/json")
                    val bodyJson = WeekBody(todayGoal = todayGoal, weekGoal = weekGoal)

                    Fuel.post("http://192.168.32.157:8000/api/chainApi/")
                        .header(header)
                        .body(requestAdapter.toJson(bodyJson))
                        .response { request, response, result ->
                            println(request)
                            println(response)
                            println(result)
                        }
                })

            alertDialog = dialogBuilder.create();
            //alertDialog.window!!.getAttributes().windowAnimations = R.style.PauseDialogAnimation
            alertDialog.show()

        }

    }

        //????????????????????????????????????????????????????????????
        fun pieChartToday(ratio: Float, view: View) {

            var ratio = ratio

            //???????????????????????????????????????//
            val dimensions = listOf("", "")//??????????????????(String???)
            val values = listOf(ratio, 100f - ratio)//?????????????????????(Float???)

            //???Entry??????????????????
            var entryList = mutableListOf<PieEntry>()
            for (i in values.indices) {
                entryList.add(
                    PieEntry(values[i], dimensions[i])
                )
            }

            //???PieDataSet??????????????????
            val pieDataSet = PieDataSet(entryList, "candle")
            //???DataSet???????????????????????????
            val colorList = listOf(Color.rgb(200, 248, 244), Color.rgb(227, 227, 227))
            pieDataSet.colors = colorList//ColorTemplate.COLORFUL_COLORS.toList()

            //???PieData???PieDataSet??????
            val pieData = PieData(pieDataSet)
            //???PieChart???PieData??????
            val pieChart = view.pieChartToday
            pieChart.data = pieData
            pieChart.description.isEnabled = false
            pieChart.centerText = "$ratio%"
            pieChart.setCenterTextSize(30f)
            pieChart.setTouchEnabled(false)
            //???Chart???????????????????????????
            pieChart.legend.isEnabled = false
            //???PieChart??????
            pieChart.invalidate()
        }

        //????????????????????????????????????????????????????????????
        fun pieChartWeek(ratio: Float, view: View) {

            val ratio = ratio

            //???????????????????????????????????????//
            val dimensions = listOf("", "")//??????????????????(String???)
            val values = listOf(ratio, 100f - ratio)//?????????????????????(Float???)

            //???Entry??????????????????
            var entryList = mutableListOf<PieEntry>()
            for (i in values.indices) {
                entryList.add(
                    PieEntry(values[i], dimensions[i])
                )
            }

            //???PieDataSet??????????????????
            val pieDataSet = PieDataSet(entryList, "candle")
            //???DataSet???????????????????????????
            val colorList = listOf(Color.rgb(200, 248, 244), Color.rgb(227, 227, 227))
            pieDataSet.colors = colorList//ColorTemplate.COLORFUL_COLORS.toList()

            //???PieData???PieDataSet??????
            val pieData = PieData(pieDataSet)
            //???PieChart???PieData??????
            val pieChart = view.pieChartWeek
            pieChart.data = pieData
            pieChart.description.isEnabled = false
            pieChart.centerText = "$ratio%"
            pieChart.setCenterTextSize(30f)
            pieChart.setTouchEnabled(false)
            //???Chart???????????????????????????
            pieChart.legend.isEnabled = false
            //???PieChart??????
            pieChart.invalidate()
        }

        //textview?????????????????????????????????
        fun setText(h1: Int, h2: Int, h3: Int, h4: Int, m1: Int, m2: Int, m3: Int, m4: Int) {


            var textTodayGoal = "?????? " + "$h1".padStart(2, '0') + "??????" + "$m1".padStart(2, '0') + "???"
            var textTodayNow = "?????? " + "$h2".padStart(2, '0') + "??????" + "$m2".padStart(2, '0') + "???"
            var textWeekGoal = "?????? " + "$h3".padStart(2, '0') + "??????" + "$m3".padStart(2, '0') + "???"
            var textWeekNow = "?????? " + "$h4".padStart(2, '0') + "??????" + "$m4".padStart(2, '0') + "???"

            val todayGoalView: TextView = TodayGoal
            todayGoalView.text = textTodayGoal

            val todayNowView: TextView = TodayNow
            todayNowView.text = textTodayNow

            val weekGoalView: TextView = WeekGoal
            weekGoalView.text = textWeekGoal

            val weekNowView: TextView = WeekNow
            weekNowView.text = textWeekNow
        }



}
