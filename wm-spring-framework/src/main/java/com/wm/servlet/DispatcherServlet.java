package com.wm.servlet;

import com.wm.annotations.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/24 .
 */
public class DispatcherServlet extends HttpServlet {

    private Properties p = new Properties();

    //扫描包下的所有class
    private List<String> classes = new ArrayList<String>();

    //有controller和service注解的类
    private Map<String,Object> ioc = new HashMap<String,Object>();

    //保存所有的Url和方法的映射关系
    private List<Handler> handleMapping = new ArrayList<Handler>();
    @Override
    public void init(ServletConfig config) throws ServletException {
        //SpringMVC核心框架环节

        //1、加载我们的spring 配置文件，通常是application.xml 我们用properties代替
        doConfig(config.getInitParameter("contextConfigLocation"));
        //2、扫描所有满足条件的controller和service
        doScanner(p.getProperty("scanPackage"));
        //3、吧这些类初始化，并且装载到IOC容器中
        doInstance();
        //4、进行依赖注入
        doAutowired();
        //5.构造HandlerMapping映射关系，将一根URL映射一个Method
        initHandlerMapping();

        System.out.println("WM SpringMVC 框架已经启动"+config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req,resp);
        }catch (Exception e){
            resp.getWriter().write("500 Exception,"+Arrays.toString(e.getStackTrace()));
        }
    }

    public void doConfig(String location) {
        InputStream fis = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            p.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doScanner(String packName) {
        //从class文件目录下找到所有class文件
        URL url = this.getClass().getClassLoader().getResource("/" + packName.replaceAll("\\.", "/"));

        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(packName + "." + file.getName());
            } else {
                String className = packName + "." + file.getName().replace(".class", "");
                classes.add(className);
            }
        }
    }

    private void doInstance() {
        if (classes.isEmpty()) return;
        try {
            for (String className : classes) {
                Class<?> clazz = Class.forName(className);
                //判断是否有这个注解
                if (clazz.isAnnotationPresent(WmController.class)) {
                    //beanName默认是首字母小写
                    String beanName = lowerFirst(clazz.getSimpleName());
                    //保存到IOC容器中
                    ioc.put(beanName,clazz.newInstance());
                }else if(clazz.isAnnotationPresent(WmService.class)){
                    //拿到这个注解所一定的名字
                    WmService service = clazz.getAnnotation(WmService.class);
                    String beanName = service.value();
                    if(!beanName.trim().equals("")){
                        ioc.put(beanName,clazz.newInstance());
                    }else{
                        beanName = lowerFirst(clazz.getSimpleName());
                        ioc.put(beanName,clazz.newInstance());
                    }
                    //获取这个class所继承的类
                    Class<?>[] interfaces = clazz.getInterfaces();
                    /**
                     *  System.out.println(apple.getClass().getSimpleName());//Apple
                     *  System.out.println(apple.getClass().getName());//返回com.test.Apple
                     */
                    for(Class <?> i :interfaces){
                        ioc.put(i.getSimpleName(),clazz.newInstance());
                    }
                }else{
                    continue;
                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void doAutowired(){
        if(ioc.isEmpty())return;
        for(Map.Entry<String,Object> entry:ioc.entrySet()){ //遍历controller和Service
            //把类下面的属性全部取出
            Field [] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field : fields){
                if(!field.isAnnotationPresent(WmAutowired.class))continue;
                WmAutowired autowired = field.getAnnotation(WmAutowired.class);
                String beanName = autowired.value().trim();
                if("".equals(beanName)){
                    beanName = field.getType().getSimpleName();  //java.lang.reflect.Field.getType()方法返回一个Class对象
                }
                //不管是否是private  私有属性，强制注入
                field.setAccessible(true);
                try {
                    /**
                     * 设置一个类里的某个属性的值
                     * 比如一个User对象里有一个String类型的name属性
                     * field 相当于name，entry.getValue相当于User，ioc.get(beanName)相当于 name的value
                     * 整句话的意思就是给User类里的name属性注入值
                     */
                    field.set(entry.getValue(),ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
            }

        }
    }


    private void initHandlerMapping(){
        if(ioc.isEmpty())return;
        for(Map.Entry<String,Object> entry:ioc.entrySet()){
            Class<?> clazz = entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(WmController.class)) continue;

            //类的URL
            String url = "";
            if(clazz.isAnnotationPresent(WmRequestMapping.class)){
                WmRequestMapping requestMapping = clazz.getAnnotation(WmRequestMapping.class);
                url = requestMapping.value();
            }
            Method [] methods = clazz.getMethods();
            for(Method method : methods){
                if(!method.isAnnotationPresent(WmRequestMapping.class)) continue;
                WmRequestMapping requestMapping = method.getAnnotation(WmRequestMapping.class);
                String regex = ("/"+url+requestMapping.value()).replaceAll("/+","/");
                Pattern pattern =Pattern.compile(regex);
                handleMapping.add(new Handler(entry.getValue(),method,pattern));
                System.out.println("mapping" + regex+","+method);
            }

        }
    }



    private void doDispatch(HttpServletRequest req ,HttpServletResponse resp){
        try {
            Handler handler = getHandler(req);
            if(handler==null){
                //如果没有匹配上就返回404
                resp.getWriter().write("404 not found");
                return;
            }

            //获取方法的参数列表
            Class<?>[] paramsTypes = handler.method.getParameterTypes();

            Object[] paramsValues = new Object[paramsTypes.length];

            Map<String,String[]> params = req.getParameterMap();

            for(Map.Entry<String,String[]> param: params.entrySet()){
                //int []a = {2,3}; ---> "[2, 3]"
                //[2, 3]--->(\[|\])这个正则的意思是匹配'['或']'    (,\s)这个正则的意思是匹配逗号加空格--》(, )如这样，匹配括号里的内容
                String value =  Arrays.toString(param.getValue()).replaceAll("\\[|\\]","").replaceAll(",\\s",",");

                //如果找到匹配对象，则开始填充值
                if(!handler.paramsIndexMapping.containsKey(param.getKey())) continue;

                int index = handler.paramsIndexMapping.get(param.getKey());
                //如果是int就变成int.其他就string
                paramsValues[index] = convert(paramsTypes[index],value);
            }
            //设置方法中的request 和 response
            Integer reqIndex = handler.paramsIndexMapping.get(HttpServletRequest.class.getName());
            if(reqIndex!=null) paramsValues[reqIndex] =req;
            Integer respIndex = handler.paramsIndexMapping.get(HttpServletResponse.class.getName());
            if(respIndex!=null) paramsValues[respIndex] = resp;
            handler.method.invoke(handler.controller,paramsValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler getHandler(HttpServletRequest req) throws Exception {
        if(handleMapping.isEmpty()) return null;

        //除（域名或ip）后面的地址
        String url = req.getRequestURI();
        System.out.println("url>>"+url);
        //如果有项目名启动的话，比如 localhost:8080/rent-server，这个rent-server
        String contextPath = req.getContextPath();
        System.out.println("contextPath>>"+contextPath);
        url = url.replace(contextPath,"").replaceAll("/+","/");
        for(Handler handler : handleMapping){
            try {
                Matcher matcher = handler.pattern.matcher(url);
                //matcher.matches完全匹配
                if(!matcher.matches())continue;
                return handler;
            }catch (Exception e){
                throw e;
            }
        }
        return null;
    }


    //首字母小写
    private String lowerFirst(String str) {
        if (str != null && !str.trim().equals("")) {
            char arr[] = str.toCharArray();
            arr[0] += 32;
            return String.valueOf(arr);
        }
        return null;
    }

    private Object convert(Class<?> type ,String value){
        if(Integer.class== type){
            return Integer.valueOf(value);
        }
        return value;
    }

    /**
     * Handler记录Contoller中的requestMapping和Method的对应关系
     */
    private class Handler{
        protected Object controller; //保存方法对应的实例
        protected Method method; //保存映射的方法
        protected Pattern pattern;
        protected Map<String,Integer> paramsIndexMapping;//参数顺序

        public Handler(Object controller, Method method, Pattern pattern ) {
            this.controller = controller;
            this.method = method;
            this.pattern = pattern;
            this.paramsIndexMapping = new HashMap<String, Integer>();
            putParamIndexMapping(method);
        }
        private void putParamIndexMapping( Method method){
            /**
             * Annotation[][] annos = method.getParameterAnnotations();

             得到的结果是一个二维数组.
             那么这个二维数组是怎么排列组合的呢?
             首先举个例子:
             @RedisScan
             public void save(@RedisSave()int id,@RedisSave()String name){

             }

             第一个参数下表为0,第二个为1

             也就是说:annos[0][0] = RedisSave
             annos[1][0] = RedisSave
             也就是说,二维数组是包含多个仅有一个值的数组.

             因为参数前可以添加多个注解,所以是二维数组,一个参数上不可以添加相同的注解,同一个注解
             可以加在不同的参数上!
             */
            Annotation[][]pa = method.getParameterAnnotations();
            for (int i = 0; i < pa.length ; i++) {
                for(Annotation annotation : pa[i]){
                    if(annotation instanceof WmRequestParams){
                        String paramName = ((WmRequestParams)annotation).value();
                        if(!"".equals(paramName.trim())){
                            paramsIndexMapping.put(paramName,i);
                        }
                    }
                }
            }

            //提取方法中的request和response参数
            // method.getParameterTypes 返回一个Class对象数组
            Class<?>[]  paramsType = method.getParameterTypes();
            for (int i = 0; i <paramsType.length ; i++) {
                Class<?> type = paramsType[i];
                if(type == HttpServletResponse.class || type == HttpServletRequest.class){
                    paramsIndexMapping.put(type.getName(),i);
                }
            }


        }
    }
}
