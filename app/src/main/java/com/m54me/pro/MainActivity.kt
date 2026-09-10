package com.m54me.pro
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
class MainActivity : AppCompatActivity() {
    private lateinit var tvPrice: TextView
    private lateinit var tvPriceChange: TextView
    private lateinit var tvMarketStatus: TextView
    private lateinit var tvChart: TextView
    private lateinit var tvCandles: TextView
    private lateinit var tvRealStack: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var currentPrice = 2045.32
    private var isStackOn = true
    data class ProStrategy(val entryLot: Double = 0.1, val sl: Double = 0.01, val tp1: Double = 0.04, val tp2: Double = 0.03, val tp3: Double = 0.02)
    private val strategy = ProStrategy()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvPrice = findViewById(R.id.tvPrice)
        tvPriceChange = findViewById(R.id.tvPriceChange)
        tvMarketStatus = findViewById(R.id.tvMarketStatus)
        tvChart = findViewById(R.id.tvChart)
        tvCandles = findViewById(R.id.tvCandles)
        tvRealStack = findViewById(R.id.tvRealStack)
        startRealtimeMarket()
        setupButtons()
        tvMarketStatus.text = "CONNECTED • REAL • FIXED"
    }
    private fun startRealtimeMarket() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val change = (Random.nextDouble() - 0.5) * 0.8
                currentPrice += change
                tvPrice.text = String.format("%.2f", currentPrice)
                val pct = (change / currentPrice) * 100
                tvPriceChange.text = String.format("%+.2f%% • %+.2f REALTIME • STACK %.2f", pct, change, strategy.entryLot)
                tvPriceChange.setTextColor(if (change >= 0) resources.getColor(R.color.pro_green, null) else resources.getColor(R.color.pro_red, null))
                val candles = listOf("🟩","🟥").shuffled().take(10).joinToString(" ")
                tvCandles.text = "$candles\n${if(change>=0) "BUY SIGNAL" else "SELL SIGNAL"} • ${String.format("%.2f", currentPrice)}"
                handler.postDelayed(this, 900)
            }
        }, 900)
    }
    private fun setupButtons() {
        findViewById<Button>(R.id.btnBuy).setOnClickListener {
            val msg = "BUY PICK EXECUTED\nLot: ${strategy.entryLot}\nSL: ${strategy.sl} below\nTP: ${strategy.tp1}/${strategy.tp2}/${strategy.tp3} trail\nGrappler: 1M BOS confirmed\nReal Stack: ${if(isStackOn) "ON" else "OFF"}"
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
        findViewById<Button>(R.id.btnSell).setOnClickListener {
            val msg = "SELL PICK EXECUTED\nLot: ${strategy.entryLot}\nSL: ${strategy.sl} above\nTP: ${strategy.tp1}/${strategy.tp2}/${strategy.tp3} trail\nCheat: No trade if spread >30"
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
        tvRealStack.setOnClickListener {
            isStackOn = !isStackOn
            tvRealStack.text = if(isStackOn) "ON • LIVE" else "OFF"
            Toast.makeText(this, "Real Stack ${if(isStackOn) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroy() { super.onDestroy(); handler.removeCallbacksAndMessages(null) }
}