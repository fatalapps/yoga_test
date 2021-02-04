package com.shandrenkoff.test.viewmodels

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.get
import androidx.lifecycle.MutableLiveData
import com.shandrenkoff.test.MainActivity
import com.shandrenkoff.test.R
import com.shandrenkoff.test.entities.CurrencyEntity
import com.shandrenkoff.test.models.RepositoryModel
import com.shandrenkoff.test.web.NetworkAssist
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import java.lang.ref.WeakReference

enum class CurrReference {
    SEND,
    GET
}

class MainViewModel {
    val currencyList: ArrayList<CurrencyEntity> = ArrayList()
    var sendVal = ""
    var getVal = ""
    var sendInd = 0
    var getInd = 0
    private lateinit var viewState: WeakReference<MainActivity>
    private lateinit var repositoryModel: RepositoryModel
    fun attachView(view: MainActivity) {
        viewState = WeakReference(view)
    }

    fun getCurrency(): Flowable<CurrencyEntity>? {
        return if (NetworkAssist(viewState.get()!!.applicationContext).hasNetworkAvailable()) repositoryModel.updateList()
        else repositoryModel.getList()
    }

    fun initRep() {
        repositoryModel = RepositoryModel()
        repositoryModel.initDb(viewState.get()!!.applicationContext)
    }

    @SuppressLint("CutPasteId")
    fun getCurrencyDialog(context: Context, refer: CurrReference): MutableLiveData<String> {
        val resultLive: MutableLiveData<String> = MutableLiveData()
        val dialog = Dialog(context)
        dialog.setTitle("Choose currency")
        dialog.setContentView(R.layout.radiobutton_dialog)
        val stringList: MutableList<String> = ArrayList() // here is list
        currencyList.forEach {
            stringList.add(it.name)
        }
        val rg: RadioGroup = dialog.findViewById(R.id.radio_group)
        rg.removeAllViews()
        for (i in stringList.indices) {
            val rb = RadioButton(context)
            rb.textSize = 20F
            rb.typeface = Typeface.DEFAULT_BOLD
            val stateList = ColorStateList(
                arrayOf(
                    intArrayOf(
                        getColor(context, R.color.colorPrimary)
                    )
                ), intArrayOf(getColor(context, R.color.colorPrimary))
            )
            rb.buttonTintList = stateList
            rb.setPadding(60, 20, 10, 20)
            rb.text = (stringList[i])
            rg.addView(rb)
        }
        var tmpClicked = currencyList[0].name
        when (refer) {
            CurrReference.SEND -> {
                rg.check(rg[sendInd].id)
            }
            CurrReference.GET -> {
                rg.check(rg[getInd].id)
            }
        }
        dialog.findViewById<Button>(R.id.ok).setOnClickListener {
            val radioButtonID: Int = rg.checkedRadioButtonId
            val radioButton: RadioButton = rg.findViewById(radioButtonID)
            tmpClicked = radioButton.text.toString()
            when (refer) {
                CurrReference.GET -> {
                    getVal = tmpClicked
                    getInd = rg.indexOfChild(radioButton)
                    resultLive.postValue(getVal)
                }
                CurrReference.SEND -> {
                    sendVal = tmpClicked
                    sendInd = rg.indexOfChild(radioButton)
                    resultLive.postValue(sendVal)
                }
            }
            stringList.clear()
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.cancel).setOnClickListener {
            stringList.clear()
            dialog.dismiss()
        }
        dialog.show()
        return resultLive
    }

    @SuppressLint("CheckResult")
    fun convert(from: String, to: String, amount: Double): Flowable<Double> {
        return Flowable.create({
            val fromValue = getValue(from)
            val toValue = getValue(to)
            it.onNext(toValue!! * amount / fromValue!!)
        }, BackpressureStrategy.LATEST)
    }

    fun getValue(find: String): Double? {
        var res = -1.0
        currencyList.forEach {
            if (find == it.name) {
                res = it.value!!
                return it.value
            }
        }
        return res
    }
}