package jp.go.mhlw.covid19r.vm

import android.content.Context
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import jp.go.mhlw.covid19r.Const
import jp.go.mhlw.covid19r.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CleoVM : ViewModel() {

    val mutableLiveAppsFlyer = MutableLiveData<MutableMap<String, Any>?>()
    val mutableLiveFace = MutableLiveData<String>()
    val mutableGadid = MutableLiveData<String>()
    val mutableLiveLink = MutableLiveData("null")

    private suspend fun takeGoogleId(context: Context) = withContext(Dispatchers.IO) {
        mutableGadid.postValue(AdvertisingIdClient.getAdvertisingIdInfo(context).id.toString())
    }

    private suspend fun takeFaceBookLink(context: Context) = suspendCoroutine{ continuation ->
        AppLinkData.fetchDeferredAppLinkData(context){ fbData ->
            continuation.resume(fbData?.targetUri.toString())
            mutableLiveFace.postValue(fbData?.targetUri.toString())
        }
    }

    private fun takeAppsFlyer(context: Context) = viewModelScope.launch{
        val appsFlyer = AppsFlyerLib.getInstance()
        val conversionListener: AppsFlyerConversionListener = object : AppsFlyerConversionListener{
            override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                mutableLiveAppsFlyer.postValue(p0)
            }

            override fun onConversionDataFail(p0: String?) {
                TODO("Not yet implemented")
            }

            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                TODO("Not yet implemented")
            }

            override fun onAttributionFailure(p0: String?) {
                TODO("Not yet implemented")
            }
        }
        appsFlyer.init(Const.appsFlyDevKey, conversionListener, context)
        appsFlyer.start(context)
    }


    fun startViewModel(context: Context){
        if (getLinkFromSharedPref(context) != "null"){
            Log.d(Const.TAG, "fun startViewModel getLinkFromSharedPref() != null ")

            mutableLiveLink.postValue(context
            .getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            .getString(Const.SHARED_LINK_NAME, "null"))
        } else {
            Log.d(Const.TAG, "fun startViewModel getLinkFromSharedPref() == null ")
            initViewModel(context)
        }
    }

    private fun initViewModel(context: Context){

        viewModelScope.launch {
            takeGoogleId(context)
            takeAppsFlyer(context)
            takeFaceBookLink(context)
            Log.d(Const.TAG, "mutableLiveFace is ${mutableLiveFace.value.toString()} " +
                    "mutableLiveAppsFlyer is ${mutableLiveAppsFlyer.value.toString()} " +
                    "mutableGadid is ${mutableGadid.value.toString()}")

            if (mutableLiveFace.value.toString() != "null"){
                createFBLink(context)
            } else if(mutableLiveAppsFlyer.value?.get("campaign") != "null"){
                createAppsFlyLink(context)
            } else {
                createOrganicLink(context)
            }
        }
    }

    private fun getLinkFromSharedPref(context: Context) : String? {
        val sharedPreferences = context.getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Const.SHARED_LINK_NAME, "null")
    }


    private fun createFBLink(context: Context){
        val tmpLink = Const.baseLink.toUri().buildUpon().apply {
            appendQueryParameter(context.getString(R.string.secure_key), context.getString(R.string.secure_get_parametr))
            appendQueryParameter(context.getString(R.string.referrer_key), "null")
            appendQueryParameter(context.getString(R.string.gadid_key), mutableGadid.value)
            appendQueryParameter(R.string.deeplink_key.toString(), mutableLiveFace.value)
            appendQueryParameter(R.string.source_key.toString(), "deeplink")
            appendQueryParameter(R.string.af_id_key.toString(), "null")
            appendQueryParameter(R.string.adset_id_key.toString(), "null")
            appendQueryParameter(R.string.campaign_id_key.toString(), "null")
            appendQueryParameter(R.string.app_campaign_key.toString(), "null")
            appendQueryParameter(R.string.adset_key.toString(), "null")
            appendQueryParameter(R.string.adgroup_key.toString(), "null")
            appendQueryParameter(R.string.orig_cost_key.toString(), "null")
            appendQueryParameter(R.string.af_siteid_key.toString(), "null")
        }.toString()
        mutableLiveLink.postValue(tmpLink)
    }

    private fun createAppsFlyLink(context: Context){
        val tmpLink = Const.baseLink.toUri().buildUpon().apply {
            appendQueryParameter(context.getString(R.string.secure_key), context.getString(R.string.secure_get_parametr))
            appendQueryParameter(context.getString(R.string.referrer_key), "null")
            appendQueryParameter(context.getString(R.string.gadid_key), mutableGadid.value)
            appendQueryParameter(context.getString(R.string.deeplink_key), "null")
            appendQueryParameter(context.getString(R.string.source_key),
                mutableLiveAppsFlyer.value?.get("media_source").toString())
            appendQueryParameter(context.getString(R.string.af_id_key),
                mutableLiveAppsFlyer.value?.get("af_id").toString())
            appendQueryParameter(context.getString(R.string.adset_id_key),
                mutableLiveAppsFlyer.value?.get("adset_id").toString())
            appendQueryParameter(context.getString(R.string.campaign_id_key),
                mutableLiveAppsFlyer.value?.get("campaign_id").toString())
            appendQueryParameter(context.getString(R.string.app_campaign_key),
                mutableLiveAppsFlyer.value?.get("campaign").toString())
            appendQueryParameter(context.getString(R.string.adset_key),
                mutableLiveAppsFlyer.value?.get("adset").toString())
            appendQueryParameter(context.getString(R.string.adgroup_key),
                mutableLiveAppsFlyer.value?.get("adgroup").toString())
            appendQueryParameter(context.getString(R.string.orig_cost_key),
                mutableLiveAppsFlyer.value?.get("orig_cost").toString())
            appendQueryParameter(context.getString(R.string.af_siteid_key),
                mutableLiveAppsFlyer.value?.get("af_siteid").toString())
        }.toString()
        mutableLiveLink.postValue(tmpLink)
    }

    private fun createOrganicLink(context: Context){
        val tmpLink = Const.baseLink.toUri().buildUpon().apply {
            appendQueryParameter(context.getString(R.string.secure_key), context.getString(R.string.secure_get_parametr))
            appendQueryParameter(R.string.referrer_key.toString(), "null")
            appendQueryParameter(R.string.gadid_key.toString(), mutableGadid.value)
            appendQueryParameter(R.string.deeplink_key.toString(), "null")
            appendQueryParameter(R.string.source_key.toString(), "null")
            appendQueryParameter(R.string.af_id_key.toString(), "null")
            appendQueryParameter(R.string.adset_id_key.toString(), "null")
            appendQueryParameter(R.string.campaign_id_key.toString(), "null")
            appendQueryParameter(R.string.app_campaign_key.toString(), "null")
            appendQueryParameter(R.string.adset_key.toString(), "null")
            appendQueryParameter(R.string.adgroup_key.toString(), "null")
            appendQueryParameter(R.string.orig_cost_key.toString(), "null")
            appendQueryParameter(R.string.af_siteid_key.toString(), "null")
        }.toString()
        mutableLiveLink.postValue(tmpLink)
    }
}