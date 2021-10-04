package com.example.myalarmclock

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimeAlertDialog: DialogFragment(){
    interface Listener{
        fun getUp()
        fun snooze()
    }
    private var listener: Listener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        when(context){
            is Listener -> listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        /*
        * Dialogを作成して戻り値にする
        * */
        //Dialogのビルダ。フラグメントを呼び出したアクテビティを取得してビルダに渡す
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("時間になりました！")//メッセージをセット
        builder.setPositiveButton("起きる"){
            dialog, which-> listener?.getUp()
        }
        builder.setNegativeButton("あと5分"){
            dialog, which-> listener?.snooze()
        }
        return builder.create()
    }
}

class DatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener{
    interface OnDateSelectedListener{
        fun onSelected(year: Int, month: Int, date: Int)
    }
    private var listener: OnDateSelectedListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        when(context){
            is OnDateSelectedListener -> listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireActivity(),this, year, month, date)
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        listener?.onSelected(p1, p2, p3)
    }
}

class TimePickerFragment: DialogFragment(), TimePickerDialog.OnTimeSetListener{
    interface OnTimeSelectedListener{
        fun onSelected(hourOfDay: Int, minute: Int)
    }
    private var listener : OnTimeSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when(context){
            is OnTimeSelectedListener -> listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return TimePickerDialog(context, this, hour, minute, true)
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        listener?.onSelected(p1, p2)
    }
}