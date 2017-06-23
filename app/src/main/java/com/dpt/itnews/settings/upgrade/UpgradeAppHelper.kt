package com.dpt.itnews.settings.upgrade

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import com.dpt.itnews.R
import java.io.File


/**
 * Created by dupengtao on 17/6/23.
 */
class UpgradeAppHelper {

    var downloadApkId :Long = -1

    private var curReceiver: BroadcastReceiver? = null

    private var hasRegister: Boolean = false

    fun downloadApk(context: Context, url: String){
        curReceiver = getReceiver()
        if(curReceiver!=null){
            context.registerReceiver(curReceiver, IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            hasRegister = true
        }

        val uri = Uri.parse(url)
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(uri)
        request.setVisibleInDownloadsUi(true)
        request.setTitle(context.resources.getString(R.string.app_name)+"更新")

        val filePath = context.externalCacheDir.absolutePath + File.separator +"kjxw_"+System.currentTimeMillis()+".apk"
        request.setDestinationUri(Uri.parse("file://"+filePath))
        downloadApkId = downloadManager.enqueue(request)
    }

//    BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            long downloadCompletedId = intent.getLongExtra(
//                    DownloadManager.EXTRA_DOWNLOAD_ID, 0);
//            // 检查是否是自己的下载队列 id, 有可能是其他应用的
//            if (enqueueId != downloadCompletedId) {
//                return;
//            }
//            DownloadManager.Query query = new DownloadManager.Query();
//            query.setFilterById(enqueueId);
//            Cursor c = mDownloadManager.query(query);
//            if (c.moveToFirst()) {
//                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
//                // 下载失败也会返回这个广播，所以要判断下是否真的下载成功
//                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
//                    // 获取下载好的 apk 路径
//                    String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
//                    // 提示用户安装
//                    promptInstall(Uri.parse("file://" + uriString));
//                }
//            }
//        }
//    };

    private fun getReceiver(): BroadcastReceiver {
        val receiver = object :BroadcastReceiver(){
            override fun onReceive(context: Context, intent: Intent) {
                val myId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
                if(downloadApkId != myId){
                    return
                }
                val query = DownloadManager.Query()
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val c = downloadManager.query(query)
                if(c.moveToFirst()){
                    val columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if(DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)){
                        val uriStr = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME))
                        val promptInstall = Intent(Intent.ACTION_VIEW)
                                .setDataAndType(Uri.parse("file://" + uriStr), "application/vnd.android.package-archive")
                        // FLAG_ACTIVITY_NEW_TASK 可以保证安装成功时可以正常打开 app
                        promptInstall.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(promptInstall)
                    }
                }
            }

        }
        return receiver
    }


    fun unRegister(context: Context){
        if(curReceiver!=null && hasRegister) {
            context.unregisterReceiver(curReceiver)
            hasRegister = false
        }
    }

}