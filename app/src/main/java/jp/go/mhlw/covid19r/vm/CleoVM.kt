package jp.go.mhlw.covid19r.vm

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import jp.go.mhlw.covid19r.Const
import jp.go.mhlw.covid19r.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CleoVM : ViewModel() {

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

    private suspend fun takeAppsFlyer(context: Context) : MutableMap<String, Any>? = suspendCoroutine{ cont ->
        AppsFlyerLib.getInstance().init(Const.appsFlyDevKey, MyConversionListener{
            cont.resume(it)
        }, context).start(context)
    }


    fun startViewModel(context: Context){
        if (getLinkFromSharedPref(context) != "null"){
            mutableLiveLink.postValue(context
            .getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            .getString(Const.SHARED_LINK_NAME, "null"))
        } else {
            initViewModel(context)
        }
    }

    private fun initViewModel(context: Context){
        viewModelScope.launch {
            takeGoogleId(context)
            takeFaceBookLink(context)
            val apps = takeAppsFlyer(context)

            Log.d(Const.TAG, "initViewModel apps is - $apps")

            if (mutableLiveFace.value.toString() != "null"){
                createFBLink(context, mutableLiveFace.value.toString())
            } else if(apps?.get("campaign") != "null"){
                val afUid = AppsFlyerLib.getInstance().getAppsFlyerUID(context)
                createAppsFlyLink(context, apps, afUid!!)
                Log.d(Const.TAG, "AppsFlyerUid id $afUid")
            } else {
                createOrganicLink(context)
            }
        }
    }

    private fun getLinkFromSharedPref(context: Context) : String? {
        val sharedPreferences = context.getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Const.SHARED_LINK_NAME, "null")
    }

    private fun createFBLink(context: Context, fbLink: String){
        val tmpLink = Const.baseLink.toUri().buildUpon().apply {
            appendQueryParameter(context.getString(R.string.secure_get_parametr), context.getString(R.string.secure_key))
            appendQueryParameter(context.getString(R.string.gadid_key), mutableGadid.value)
            appendQueryParameter(context.getString(R.string.deeplink_key), fbLink)
            appendQueryParameter(context.getString(R.string.source_key), "deeplink")
            appendQueryParameter(context.getString(R.string.af_id_key), "null")
            appendQueryParameter(context.getString(R.string.adset_id_key), "null")
            appendQueryParameter(context.getString(R.string.campaign_id_key), "null")
            appendQueryParameter(context.getString(R.string.app_campaign_key), "null")
            appendQueryParameter(context.getString(R.string.adset_key), "null")
            appendQueryParameter(context.getString(R.string.adgroup_key), "null")
            appendQueryParameter(context.getString(R.string.orig_cost_key), "null")
            appendQueryParameter(context.getString(R.string.af_siteid_key), "null")
        }.toString()
        mutableLiveLink.postValue(tmpLink)
        Log.d(Const.TAG, "createFBLink")
        OneSignal.sendTag("key2", fbLink.substringAfter("://").substringBefore("/"))

        Log.d(Const.TAG, " One signal is ${fbLink.substringAfter("://").substringBefore("/")}")
    }

    private fun createAppsFlyLink(context: Context, apps: MutableMap<String, Any>?, appsFlyerUid: String){
        Log.d(Const.TAG, "Method createAppsFlyLink, apps is: ${apps.toString()}")
        val tmpLink = Const.baseLink.toUri().buildUpon().apply {
            appendQueryParameter(context.getString(R.string.secure_get_parametr), context.getString(R.string.secure_key))
            appendQueryParameter(context.getString(R.string.gadid_key), mutableGadid.value)
            appendQueryParameter(context.getString(R.string.deeplink_key), "null")
            appendQueryParameter(context.getString(R.string.source_key),
                apps?.get("media_source").toString())
            appendQueryParameter(context.getString(R.string.af_id_key),
                appsFlyerUid)
            appendQueryParameter(context.getString(R.string.adset_id_key),
                apps?.get("adset_id").toString())
            appendQueryParameter(context.getString(R.string.campaign_id_key),
                apps?.get("campaign_id").toString())
            appendQueryParameter(context.getString(R.string.app_campaign_key),
                apps?.get("campaign").toString())
            appendQueryParameter(context.getString(R.string.adset_key),
                apps?.get("adset").toString())
            appendQueryParameter(context.getString(R.string.adgroup_key),
                apps?.get("adgroup").toString())
            appendQueryParameter(context.getString(R.string.orig_cost_key),
                apps?.get("orig_cost").toString())
            appendQueryParameter(context.getString(R.string.af_siteid_key),
                apps?.get("af_siteid").toString())
        }.toString()

        mutableLiveLink.postValue(tmpLink)
        OneSignal.sendTag("key2", apps?.get("campaign").toString().substringBefore("_"))
        Log.d(Const.TAG, "createAppsFlyLink")
    }

    private fun createOrganicLink(context: Context){
        val tmpLink = Const.baseLink.toUri().buildUpon().apply {
            appendQueryParameter(context.getString(R.string.secure_get_parametr), context.getString(R.string.secure_key))
            appendQueryParameter(context.getString(R.string.gadid_key), mutableGadid.value)
            appendQueryParameter(context.getString(R.string.deeplink_key), "null")
            appendQueryParameter(context.getString(R.string.source_key), "null")
            appendQueryParameter(context.getString(R.string.af_id_key), "null")
            appendQueryParameter(context.getString(R.string.adset_id_key), "null")
            appendQueryParameter(context.getString(R.string.campaign_id_key), "null")
            appendQueryParameter(context.getString(R.string.app_campaign_key), "null")
            appendQueryParameter(context.getString(R.string.adset_key), "null")
            appendQueryParameter(context.getString(R.string.adgroup_key), "null")
            appendQueryParameter(context.getString(R.string.orig_cost_key), "null")
            appendQueryParameter(context.getString(R.string.af_siteid_key), "null")
        }.toString()
        mutableLiveLink.postValue(tmpLink)
        OneSignal.sendTag("key2", "organic")
        Log.d(Const.TAG, "createAppsFlyLink")
    }


    inner class MyConversionListener(private val block: (MutableMap<String, Any>?) -> Unit) : AppsFlyerConversionListener{
        override fun onConversionDataSuccess(p0: MutableMap<String, Any>?){
            block(p0)
        }

        override fun onConversionDataFail(p0: String?) {

        }

        override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
            TODO("Not yet implemented")
        }

        override fun onAttributionFailure(p0: String?) {
            TODO("Not yet implemented")
        }
    }
}