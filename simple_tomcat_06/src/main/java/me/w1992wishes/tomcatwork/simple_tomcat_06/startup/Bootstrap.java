package me.w1992wishes.tomcatwork.simple_tomcat_06.startup;

import me.w1992wishes.tomcatwork.simple_tomcat_06.Lifecycle;
import me.w1992wishes.tomcatwork.simple_tomcat_06.LifecycleListener;
import me.w1992wishes.tomcatwork.simple_tomcat_06.Loader;
import me.w1992wishes.tomcatwork.simple_tomcat_06.connector.http.HttpConnector;
import me.w1992wishes.tomcatwork.simple_tomcat_06.container.*;
import me.w1992wishes.tomcatwork.simple_tomcat_06.container.impl.*;
import me.w1992wishes.tomcatwork.simple_tomcat_06.valves.ClientIPLoggerValve;
import me.w1992wishes.tomcatwork.simple_tomcat_06.valves.HeaderLoggerValve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wanqinfeng on 2017/2/22.
 */
public class Bootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {

        Context context = new SimpleContext();

        Loader loader = new SimpleLoader();
        context.setLoader(loader);

        Valve valve1 = new HeaderLoggerValve();
        Valve valve2 = new ClientIPLoggerValve();
        ((Pipeline) context).addValve(valve1);
        ((Pipeline) context).addValve(valve2);

        Wrapper wrapper1 = new SimpleWrapper();
        wrapper1.setName("Primitive");
        wrapper1.setServletClass("PrimitiveServlet");
        Wrapper wrapper2 = new SimpleWrapper();
        wrapper2.setName("Modern");
        wrapper2.setServletClass("ModernServlet");
        context.addChild(wrapper1);
        context.addChild(wrapper2);

        LifecycleListener listener = new SimpleContextLifecycleListener();
        ((Lifecycle) context).addLifecycleListener(listener);

        Mapper mapper = new SimpleContextMapper();
        mapper.setProtocol("http");
        context.addMapper(mapper);

        // context.addServletMapping(pattern, name);
        context.addServletMapping("/Primitive", "Primitive");
        context.addServletMapping("/Modern", "Modern");

        HttpConnector connector = new HttpConnector();
        connector.setContainer(context);

        try {
            connector.initialize();
            connector.start();
            ((Lifecycle) context).start();

            // make the application wait until we press a key.
            System.in.read();
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new RuntimeException();
        }
    }

}
