package kale.lib.eventbus;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import kale.lib.eventbus.reflect.Reflect;


/**
 * @author Jack Tony
 * @date 2015/11/7
 */
class ParamsHandler {

    private static final boolean DEBUG = false;

    private static final String TAG = EventBus.class.getSimpleName();

    private static final String PARAMS_COUNT = "params_count";

    public static Intent generateIntent(final String tag, final Object[] params) {
        Intent intent = new Intent();
        intent.setAction(tag);

        if (params[0] instanceof EventBus.NullParam) {
            if (DEBUG) {
                Log.d(TAG, "无参数");
            }
            intent.putExtra(PARAMS_COUNT, 0);
        } else {
            intent.putExtra(PARAMS_COUNT, params.length);
            Object param;
            String key;

            for (int i = 0, paramsLength = params.length; i < paramsLength; i++) {
                param = params[i];
                key = String.valueOf(i);

                if (DEBUG) Log.d(TAG, "Post: param type is " + param);

                if (param == null) {
                    // null也是参数的一种
                    if (DEBUG) Log.d(TAG, "param type is null");
                } 
                else if (param instanceof CharSequence) {
                    intent.putExtra(key, (CharSequence) param);
                } 
                else if (param instanceof CharSequence[]) {
                    intent.putExtra(key, (CharSequence[]) param);
                } 
                else if (param instanceof Parcelable) {
                    intent.putExtra(key, (Parcelable) param);
                } 
                else if (param instanceof Parcelable[]) {
                    intent.putExtra(key, (Parcelable[]) param);
                } 
                else if (param instanceof Serializable[]) {
                    intent.putExtra(key, (Serializable[]) param);
                } 
                else if (param instanceof Serializable) {
                    intent.putExtra(key, (Serializable) param);
                } 
                else {
                    throw new IllegalArgumentException("参数必须是intent可以存入的类型");
                }
            }
        }
        return intent;
    }

    public static void callMethod(Object subscriber, List<Method> methods, Intent intent) {
        int paramsCount = intent.getIntExtra(PARAMS_COUNT, 0);

        for (Method method : methods) {
            if (paramsCount != method.getParameterTypes().length) {
                // 参数个数不同
                continue;
            }
            if (paramsCount == 0) {
                // 无参数，无需进行参数匹配，已经匹配成功
                Reflect.on(subscriber).call(method.getName());
                break;
            } else {
                // 有参数
                Object[] params = getParamsFromIntent(intent, paramsCount);
                if (isMatch(method, params)) {
                    // 传入的参数和接收的参数的类型完全一致，匹配成功
                    Reflect.on(subscriber).call(method.getName(), params);
                    break;
                }
            }
        }
    }

    private static Object[] getParamsFromIntent(Intent intent, int paramsCount) {
        Object[] params = new Object[paramsCount];
        for (int i = 0; i < paramsCount; i++) {
            params[i] = intent.getExtras().get(i + "");
        }
        return params;
    }

    private static boolean isMatch(Method method, Object[] params) {
        Class<?>[] targetClsSArr = method.getParameterTypes();
        for (int i = 0, paramsLength = params.length; i < paramsLength; i++) {
            if (params[i] == null) {
                continue;
            }
            Class<?> targetClz = Reflect.wrapper(targetClsSArr[i]);
            if (DEBUG) {
                Log.d(TAG, "收到 param type = " + params[i].getClass().getCanonicalName());
                Log.d(TAG, "应该 param type = " + targetClz.getCanonicalName());
            }

            if (!targetClz.isAssignableFrom(params[i].getClass())) {
                if (DEBUG) Log.e(TAG, "callMethod: 不匹配！！！");
                return false;
            } else {
                if (DEBUG) Log.d(TAG, "callMethod: 匹配成功");
            }
        }
        return true;
    }

}