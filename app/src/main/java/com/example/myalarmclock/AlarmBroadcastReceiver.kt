package com.example.myalarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //Toast.makeText(context,"アラームを受信しました", Toast.LENGTH_SHORT).show()
        val mainIntent = Intent(context, MainActivity::class.java)
            .putExtra("onReceive", true)//アクティビティがBroadcastReceiverから起動されたことがわかるための情報
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //onReceiveメソッドに渡されるContextを使ってstartActivityメソッドを起動
        context.startActivity(mainIntent)
    }
}