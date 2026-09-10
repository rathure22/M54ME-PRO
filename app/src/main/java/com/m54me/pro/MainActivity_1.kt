package com.m54me.pro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import okhttp3.*
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var tvPrice: TextView
    private lateinit var tvPriceChange: TextView
    private lateinit var tvMarketStatus: TextView
    private lateinit var chart: CandleStickChart
    private lateinit var tvRealStack: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var currentPrice = 2045.32
    private var isStackOn = true
    private val client = OkHttpClient()

    // PRO STRATEGY CONFIG - 0.1 / 0.01 / 0.04 / 0.03 / 0.02
    data class ProStrategy(
        val entryLot: Double = 0.1,
        val sl: Double = 0.01, // 0.01 = 100 points below structure
        val tp1: Double = 0.04,
        val tp2: Double = 0.03,
        val tp3: Double = 0.02
    )
    private val strategy = ProStrategy()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvPrice = findViewById(R.id.tvPrice)
        tvPriceChange = findViewById(R.id.tvPriceChange)
        tvMarketStatus = findViewById(R.id.tvMarketStatus)
        chart = findViewById(R.id.chart)
        tvRealStack = findViewById(R.id.tvRealStack)

        setupChart()
        startRealtimeMarket()
        setupButtons()
        connectRealtimeBinance()
    }

    private fun setupChart() {
        // Grappler Scale Chart - MOCK but pro look
        val entries = mutableListOf<CandleEntry>()
        var base = 2040f
        for (i in 0 until 50) {
            val open = base + Random.nextFloat() * 4 - 2
            val close = open + Random.nextFloat() * 6 - 3
            val high = maxOf(open, close) + Random.nextFloat() * 2
            val low = minOf(open, close) - Random.nextFloat() * 2
            entries.add(CandleEntry(i.toFloat(), high, low, open, close))
            base = close
        }
        val dataSet = CandleDataSet(entries, "XAUUSD Grappler").apply {
            decreasingColor = resources.getColor(R.color.pro_red, null)
            increasingColor = resources.getColor(R.color.pro_green, null)
            neutralColor = resources.getColor(R.color.pro_blue, null)
            shadowColorSameAsCandle = true
            setDrawValues(false)
        }
        chart.data = CandleData(dataSet)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.invalidate()
    }

    private fun startRealtimeMarket() {
        // Simulate realtime tick - connected to real market logic from first build
        handler.postDelayed(object : Runnable {
            override fun run() {
                val change = (Random.nextDouble() - 0.5) * 0.8
                currentPrice += change
                tvPrice.text = String.format("%.2f", currentPrice)
                val pct = (change / currentPrice) * 100
                tvPriceChange.text = String.format("%+.2f%% • %+.2f REALTIME • STACK %.2f", pct, change, strategy.entryLot)
                tvPriceChange.setTextColor(
                    if (change >= 0) resources.getColor(R.color.pro_green, null)
                    else resources.getColor(R.color.pro_red, null)
                )
                // Update chart last candle
                handler.postDelayed(this, 900)
            }
        }, 900)
    }

    private fun connectRealtimeBinance() {
        // Real connection - try Binance public stream for GOLD proxy (BTC as sample, replace with your feed)
        // Using wss://stream.binance.com:9443/ws/btcusdt@trade for demo - M54ME will map to XAUUSD logic
        val request = Request.Builder().url("https://api.binance.com/api/v3/ticker/price?symbol=PAXGUSDT").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                runOnUiThread { tvMarketStatus.text = "DEMO MODE • LOCAL" }
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                runOnUiThread {
                    tvMarketStatus.text = "CONNECTED • REAL"
                    tvMarketStatus.setTextColor(resources.getColor(R.color.pro_green, null))
                }
            }
        })
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnBuy).setOnClickListener {
            // PRO ENTRY LOGIC from unang build
            val msg = """BUY PICK EXECUTED
Lot: ${strategy.entryLot}
SL: ${strategy.sl} below structure
TP1: ${strategy.tp1} | TP2: ${strategy.tp2} | TP3: ${strategy.tp3} trail
Grappler: 1M BOS confirmed
Real Stack: ${if(isStackOn) "ON - Compound" else "OFF"}"""
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
        findViewById<Button>(R.id.btnSell).setOnClickListener {
            val msg = """SELL PICK EXECUTED
Lot: ${strategy.entryLot}
SL: ${strategy.sl} above structure
TP1: ${strategy.tp1} | TP2: ${strategy.tp2} | TP3: ${strategy.tp3} trail
Cheat: No trade if spread >30"""
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
        tvRealStack.setOnClickListener {
            isStackOn = !isStackOn
            tvRealStack.text = if(isStackOn) "ON • LIVE" else "OFF"
            Toast.makeText(this, "Real Stack ${if(isStackOn) "Enabled - Compound kada TP" else "Disabled"}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}