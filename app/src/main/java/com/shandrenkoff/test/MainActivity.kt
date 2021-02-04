package com.shandrenkoff.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.shandrenkoff.test.viewmodels.CurrReference
import com.shandrenkoff.test.viewmodels.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var wait = false
    var viewModel = MainViewModel()
    lateinit var lifecycleOwner: LifecycleOwner

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.title)
        lifecycleOwner = this
        viewModel.attachView(this)
        viewModel.initRep()
        viewModel.getCurrency()!!
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                Log.e("tag", it.name + "=" + it.value + " on mainThread")
                if (spinner_get.text.isNullOrEmpty()) {
                    spinner_get.text = it.name
                    viewModel.getVal = it.name
                }
                if (spinner_send.text.isNullOrEmpty()) {
                    spinner_send.text = it.name
                    viewModel.sendVal = it.name
                }
                viewModel.currencyList.add(it)
            }
        spinner_get_area.setOnClickListener {
            val dialogLive = viewModel.getCurrencyDialog(this, CurrReference.GET)
            dialogLive.observe(lifecycleOwner, Observer { get_res ->
                spinner_get.text = get_res
                get_amount_t.setText("")
                send_amount_t.setText("")
            })
        }
        spinner_send_area.setOnClickListener {
            val dialogLive = viewModel.getCurrencyDialog(this, CurrReference.SEND)
            dialogLive.observe(lifecycleOwner, Observer { send_res ->
                spinner_send.text = send_res
                send_amount_t.setText("")
                get_amount_t.setText("")
            })
        }
        val sendListener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && !wait) {
                    val sendAmount = s.toString().toDouble()
                    Log.e("", sendAmount.toString())
                    viewModel.convert(from = viewModel.sendVal, to = viewModel.getVal, sendAmount)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { result ->
                            Log.e(
                                "",
                                viewModel.sendVal + " to " + viewModel.getVal + " = " + result.toString()
                            )
                            wait = true
                            get_amount_t.setText(String.format("%.2f", result))
                            wait = false
                        }
                }
            }
        }
        val getListener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && !wait) {
                    val getAmount = s.toString().toDouble()
                    Log.e("", getAmount.toString())
                    viewModel.convert(from = viewModel.getVal, to = viewModel.sendVal, getAmount)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { result ->
                            Log.e(
                                "",
                                viewModel.getVal + " to " + viewModel.sendVal + " = " + result.toString()
                            )
                            wait = true
                            send_amount_t.setText(String.format("%.2f", result))
                            wait = false
                        }
                }
            }
        }
        send_amount_t.addTextChangedListener(sendListener)
        get_amount_t.addTextChangedListener(getListener)
    }

}