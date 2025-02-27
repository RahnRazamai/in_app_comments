package hr.prijavituriste.in_app_comments;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;

/** InAppCommentsPlugin */
public class InAppCommentsPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, ActivityResultListener {
  private MethodChannel channel;
  private Result result;
  private Activity activity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "in_app_comments");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    this.result = result;
    if (call.method.equals("requestComments")) {
      if (activity == null) {
        result.error("NO_ACTIVITY", "Activity is null", null);
        return;
      }
      Intent intent = new Intent("com.huawei.appmarket.intent.action.guidecomment");
      intent.setPackage("com.huawei.appmarket");
      activity.startActivityForResult(intent, 1001);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    activity = activityPluginBinding.getActivity();
    activityPluginBinding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
    activity = activityPluginBinding.getActivity();
    activityPluginBinding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 1001 && result != null) {
      switch (resultCode) {
        case 102:
        case 103:
          result.success(resultCode);
          break;
        case 0:
          result.error("0", "error", "Unknown error");
          break;
        case 101:
          result.error("101", "error", "The app has not been released on AppGallery");
          break;
        case 104:
          result.error("104", "error", "The HUAWEI ID sign-in status is invalid");
          break;
        case 105:
          result.error("105", "error", "The user does not meet the conditions for displaying the comment pop-up");
          break;
        case 106:
          result.error("106", "error", "The commenting function is disabled");
          break;
        case 107:
          result.error("107", "error", "The in-app commenting service is not supported");
          break;
        case 108:
          result.error("108", "error", "The user canceled the comment");
          break;
        default:
          result.error(String.valueOf(resultCode), "Unknown error code", null);
          break;
      }
      result = null; // Clear the result reference after handling it
      return true;
    }
    return false;
  }
}
