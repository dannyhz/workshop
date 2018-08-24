package cn.evun.sweet.core.service;

import cn.evun.sweet.common.util.reflect.MethodInvoker;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.exception.SweetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 对登记到其中的服务可以实现根据id直接调用。<br/>
 *
 * @author yangw
 * @since V1.0.0
 */
public class RegistyServiceInvoker {

    public static Object invoker(String serviceId, Object... args) {
        return RegistyServiceInvoker.getInstance().doInvoker(serviceId, args);
    }

    protected static final Logger LOGGER = LogManager.getLogger();

    private final Map<String, MethodInvoker> services = new HashMap<String, MethodInvoker>();

    private static class RegistyServiceInvokerHoler {
        private static final RegistyServiceInvoker INSTANCE = new RegistyServiceInvoker();
    }

    public static final RegistyServiceInvoker getInstance() {
        return RegistyServiceInvokerHoler.INSTANCE;
    }

    private RegistyServiceInvoker() {
    }

    /**
     * 根据服务标识调用服务
     *
     * @param serviceId 服务标识
     * @param args      调用需要的参数
     */
    public Object doInvoker(String serviceId, Object... args) {
        MethodInvoker service = this.services.get(serviceId);
        if (service != null) {
            service.setArguments(args);
            try {
                return service.invoke();
            } catch (Exception ex) {
                LOGGER.error("Exception when invoke service {}", serviceId, ex);
                if (ex instanceof InvocationTargetException) {
                    Throwable t = ((InvocationTargetException) ex).getTargetException();
                    if (t instanceof SweetException) {
                        throw (SweetException) t;
                    }
                }
                throw new SweetException(R.exception.excode_registyservice_invokefailed);
            }
        } else {
            throw new SweetException(R.exception.excode_registyservice_notexist);
        }
    }

    /**
     * 注册服务方法
     */
    public void registyService(String id, MethodInvoker service) {
        if (this.services.get(id) != null) {
            LOGGER.error("Registyservice mapping-id '{}' registered more than once time. Multiple mapping-id is unsupport");
            return;
        }
        LOGGER.info("Mapped registyservice [{}] onto [{}]", id, service.getPreparedMethod());
        this.services.put(id, service);
    }

    public void reset() {
        this.services.clear();
    }

    public int getServicesSize() {
        return this.services.size();
    }

    /**
     * 根据服务ID获取Method
     *
     * @param serviceId 服务标识
     * @return 反射对象Method
     */
    public Method doGetMethod(String serviceId) {
        MethodInvoker service = this.services.get(serviceId);
        if (service != null) {
            return service.getPreparedMethod();
        } else {
            throw new SweetException(R.exception.excode_registyservice_notexist);
        }
    }

    public static Method getMethod(String serviceId) {
        return RegistyServiceInvoker.getInstance().doGetMethod(serviceId);
    }

}
