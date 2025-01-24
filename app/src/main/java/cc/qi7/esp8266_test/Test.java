package cc.qi7.esp8266_test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author AHeng
 * @date 2024/08/08 21:51
 */
public class Test {
    private static final String TAG = "日志";
    
    public static void main(String[] args) {
        
        class Person {
            private String name;
            private int age;
            
            public Person(String name, int age) {
                this.name = name;
                this.age = age;
            }
            
            public String getName() {
                return name;
            }
            
            public void setName(String name) {
                this.name = name;
            }
            
            public int getAge() {
                return age;
            }
            
            public void setAge(int age) {
                this.age = age;
            }
            
            @Override
            public String toString() {
                return new StringJoiner(", ", Person.class.getSimpleName() + "[", "]")
                               .add("name='" + name + "'")
                               .add("age=" + age)
                               .toString();
            }
        }
        
        CopyOnWriteArrayList<Person> personList = new CopyOnWriteArrayList<>();
        personList.add(new Person("张三", 18));
        personList.add(new Person("李四", 19));
        personList.add(new Person("王五", 20));
        
        // 模拟频繁修改元素
        new Thread(() -> {
            try {
                while (true) {
                    Iterator<Person> iterator = personList.iterator();
                    while (iterator.hasNext()) {
                        Person person = iterator.next();
                        person.setAge(new Random().nextInt(10));
                    }
                    
                    // for (Person person : personList) {
                    //     person.setAge(new Random().nextInt(10));
                    // }
                    
                    System.out.println(personList.toString());
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // 保留中断状态
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }).start();
        
        
        // 模拟新增元素
        new Thread(() -> {
            while (true) {
                try {
                    personList.add(new Person("zjh", 18));
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        
        // 模拟删除元素
        new Thread(() -> {
            while (true) {
                try {
                    if (personList.isEmpty()) {
                        continue;
                    }
                    personList.remove(0);
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        
        
    }
    
    public class test {
        private float a = 0.1f;
        
        public test() {
            while (true) {
                a = a + 0.1f;
            }
        }
        
        public float getA() {
            return a;
        }
    }
    
/*    public static void main(String[] args) {
        
        
        System.out.println(BaseUtils.isJson("{\"id\":\"@5@\",\"params\":{\"lightSwitch\":@3@,\"temperature\":@1@,\"test\":\"@2@\"},\"method\":\"@4@\"}"));
        System.out.println(BaseUtils.isJson("000"));
        System.out.println(BaseUtils.isJson("{\"1\":0}"));
        System.out.println(BaseUtils.isJson("{1:0}"));
        
        
        // Map<String, CIB> cardItemBeanMap = new LinkedHashMap<>();
        // cardItemBeanMap.put("hhhhhhhh", new CIB("@1@", "@1@", "@1@", "@1@"));
        // cardItemBeanMap.put("哈哈哈哈哈", new CIB("@2@", "@2@", "@2@", "@2@"));
        // cardItemBeanMap.put("十大网", new CIB("@3@", "@3@", "@3@", "@3@"));
        // cardItemBeanMap.put("而二哥仍然", new CIB("@4@", "@4@", "@4@", "@4@"));
        //
        // CardGroupBean cardGroupBean = new CardGroupBean();
        // cardGroupBean.setPlaceholderModelContent("{\"id\":\"@5@\",\"params\":{\"lightSwitch\":@3@,\"temperature\":@1@,\"test\":\"@2@\"},\"method\":\"@4@\"}");
        // cardGroupBean.setPlaceholderSplit(new String[] {"id\":", "{\"id\":\"", "\",\"params\":{\"lightSwitch\":", ",\"temperature\":", ",\"test\":\"", "\"},\"method\":\"", "\"}"});
        // cardGroupBean.setCardItemBeanMap(cardItemBeanMap);
        
        
        // List<CardGroupBean> cardGroupBeans = new ArrayList<>();
        // for (int index = 0; index < 20; index++) {
        //     Map<String, CIB> cardItemBeanMap = new LinkedHashMap<>();
        //     cardItemBeanMap.put("hhhhhhhh" + index, new CIB("@1@", "@1@", "@1@", "@1@"));
        //     cardItemBeanMap.put("哈哈哈哈哈" + index, new CIB("@2@", "@2@", "@2@", "@2@"));
        //     cardItemBeanMap.put("十大网" + index, new CIB("@3@", "@3@", "@3@", "@3@"));
        //     cardItemBeanMap.put("sdsedew" + index, new CIB("@3@", "@3@", "@3@", "@3@"));
        //     cardItemBeanMap.put("323232" + index, new CIB("@3@", "@3@", "@3@", "@3@"));
        //     cardItemBeanMap.put("afefwe" + index, new CIB("@3@", "@3@", "@3@", "@3@"));
        //
        //     CardGroupBean cardGroupBean = new CardGroupBean();
        //     cardGroupBean.setPlaceholderModelContent("{\"id\":\"@5@\",\"params\":{\"lightSwitch\":@3@,\"temperature\":@1@,\"test\":\"@2@\"},\"method\":\"@4@\"}");
        //     cardGroupBean.setPlaceholderSplit(new String[] {"id\":", "{\"id\":\"", "\",\"params\":{\"lightSwitch\":", ",\"temperature\":", ",\"test\":\"", "\"},\"method\":\"", "\"}"});
        //     cardGroupBean.setCardItemBeanMap(cardItemBeanMap);
        //
        //     cardGroupBeans.add(cardGroupBean);
        // }
        // BaseUtils.getRunTime(() -> {
        //     try {
        //         // 序列化
        //         FileOutputStream fos = new FileOutputStream("test.ser");
        //         ObjectOutputStream oos = new ObjectOutputStream(fos);
        //         oos.writeObject(cardGroupBeans);
        //         oos.close();
        //         fos.close();
        //     } catch (Exception e) {
        //         System.out.println("序列化: " + e);
        //         throw new RuntimeException(e);
        //     }
        // });
        //
        //
        // BaseUtils.getRunTime(() -> {
        //     try {
        //         // 反序列化
        //         FileInputStream fis = new FileInputStream("test.ser");
        //         ObjectInputStream ois = new ObjectInputStream(fis);
        //         List<CardGroupBean> deserializedBean = (List<CardGroupBean>) ois.readObject();
        //         ois.close();
        //         fis.close();
        //
        //         // System.out.println("反序列化结果" + deserializedBean);
        //     } catch (Exception e) {
        //         System.out.println("反序列化: " + e);
        //         throw new RuntimeException(e);
        //     }
        // });
        
        
        // BaseUtils.getRunTime(() -> {
        //     try {
        //         List<CardItemBean> cardItemBean = new ArrayList<>();
        //
        //         for (int i = 0; i < 40; i++) {
        //             CardItemBean tempBean = new CardItemBean();
        //             tempBean.setComment("11" + i);
        //             tempBean.setSuffix("11" + i);
        //             tempBean.setPrefix("11" + i);
        //             tempBean.setPlaceholder("@1@" + i);
        //             tempBean.setPlaceholderModelContent("@1@" + i);
        //             tempBean.setPlaceholderSplit(new String[] {"@1@" + i});
        //             cardItemBean.add(tempBean);
        //         }
        //
        //         List<CardItemBean> cardItemBeans1 = Clone.deepCopyList(cardItemBean);
        //     } catch (Exception e) {
        //         System.out.println("e = " + e);
        //     }
        // });
        
        
        List<CardItemBean> qq = new ArrayList<>();
        CardItemBean qqcardItemBean1 = new CardItemBean();
        qqcardItemBean1.setPrefix("11");
        qqcardItemBean1.setSuffix("11");
        qqcardItemBean1.setPlaceholder("@1@");
        qqcardItemBean1.setComment("11");
        
        CardItemBean qqcardItemBean2 = new CardItemBean();
        qqcardItemBean2.setPrefix("22");
        qqcardItemBean2.setSuffix("22");
        qqcardItemBean2.setPlaceholder("@2@");
        qqcardItemBean2.setComment("22");
        
        CardItemBean qqcardItemBean3 = new CardItemBean();
        qqcardItemBean3.setPrefix("33");
        qqcardItemBean3.setSuffix("33");
        qqcardItemBean3.setPlaceholder("@3@");
        qqcardItemBean3.setComment("33");
        
        CardItemBean qqcardItemBean4 = new CardItemBean();
        qqcardItemBean4.setPrefix("44");
        qqcardItemBean4.setSuffix("44");
        qqcardItemBean4.setPlaceholder("@4@");
        qqcardItemBean4.setComment("44");
        
        qq.add(qqcardItemBean4);
        qq.add(qqcardItemBean2);
        qq.add(qqcardItemBean3);
        qq.add(qqcardItemBean1);
        
        
        List<CardItemBean> ww = new ArrayList<>();
        CardItemBean cardItemBean1 = new CardItemBean();
        cardItemBean1.setPrefix("11");
        cardItemBean1.setSuffix("11");
        cardItemBean1.setPlaceholder("@1@");
        cardItemBean1.setComment("11");
        
        CardItemBean cardItemBean2 = new CardItemBean();
        cardItemBean2.setPrefix("22");
        cardItemBean2.setSuffix("22");
        cardItemBean2.setPlaceholder("@2@");
        cardItemBean2.setComment("22");
        
        CardItemBean cardItemBean3 = new CardItemBean();
        cardItemBean3.setPrefix("33");
        cardItemBean3.setSuffix("33");
        cardItemBean3.setPlaceholder("@3@");
        cardItemBean3.setComment("33");
        
        CardItemBean cardItemBean4 = new CardItemBean();
        cardItemBean4.setPrefix("445");
        cardItemBean4.setSuffix("4455");
        cardItemBean4.setPlaceholder("@4@");
        cardItemBean4.setComment("55");
        
        ww.add(cardItemBean3);
        ww.add(cardItemBean2);
        ww.add(cardItemBean4);
        ww.add(cardItemBean1);
        
        
        // Map<String, CardItemBean> placeholderValueMap = new LinkedHashMap<>();
        // placeholderValueMap.put("@1@", cardItemBean1);
        // placeholderValueMap.put("@2@", cardItemBean2);
        // placeholderValueMap.put("@3@", cardItemBean3);
        // placeholderValueMap.put("@4@", cardItemBean4);
        // System.out.println(TAG + placeholderValueMap);
        
        
        Map<String, CardItemBean> identifierMap = qq.stream().collect(Collectors.toMap(CardItemBean::getPlaceholder, Function.identity()));
        
        // 创建一个根据标识符排序的列表
        List<CardItemBean> sortedList = new ArrayList<>();
        for (CardItemBean bean : qq) {
            sortedList.add(identifierMap.get(bean.getPlaceholder()));
        }
        
        
        System.out.println(qq);
        // System.out.println("处理: " + ww);
        System.out.println(sortedList);
        
        
    }*/
    
    // public static void main(String[] args) {
    //     LinkedBlockingQueue<Map<String, String>> linkedBlockingQueue = new LinkedBlockingQueue<>();
    //
    //
    //     // 入对
    //     new Thread(() -> {
    //         try {
    //             String sourceJson = "{\n" +
    //                                         "  \"id\": 123,\n" +
    //                                         "  \"version\": \"1.0\",\n" +
    //                                         "  \"params\": {\n" +
    //                                         "    \"LightSwitch\": 1,\n" +
    //                                         "    \"temperature\": 48.4,\n" +
    //                                         "    \"test\": 1\n" +
    //                                         "  },\n" +
    //                                         "  \"method\": \"thing.service.property.set\"\n" +
    //                                         "}";
    //
    //             String modelJson = "{\n" +
    //                                        "  \"id\": @1@,\n" +
    //                                        "  \"version\": \"@2@\",\n" +
    //                                        "  \"params\": {\n" +
    //                                        "    \"LightSwitch\": @3@,\n" +
    //                                        "    \"temperature\": @4@,\n" +
    //                                        "    \"test\": @5@\n" +
    //                                        "  },\n" +
    //                                        "  \"method\": \"@6@\"\n" +
    //                                        "}";
    //
    //             while (true) {
    //                 Map<String, String> placeholderValueMap = getPlaceholderValueMap(sourceJson, modelJson);
    //                 linkedBlockingQueue.offer(placeholderValueMap);
    //                 Thread.sleep(100);
    //             }
    //
    //         } catch (Exception e) {
    //             System.out.println(e.toString());
    //             throw new RuntimeException(e);
    //         }
    //     }, "A").start();
    //
    //
    //     // 出队
    //     new Thread(() -> {
    //         try {
    //             while (true) {
    //                 Map<String, String> take = linkedBlockingQueue.take();
    //                 System.out.println("\n\n几个: " + take.size());
    //                 System.out.println(take.get("@1@"));
    //                 System.out.println(take.get("@2@"));
    //                 System.out.println(take.get("@3@"));
    //                 System.out.println(take.get("@4@"));
    //                 System.out.println(take.get("@5@"));
    //                 System.out.println(take.get("@6@"));
    //                 Thread.sleep(1500);
    //             }
    //         } catch (InterruptedException e) {
    //             System.out.println(e.toString());
    //             throw new RuntimeException(e);
    //         }
    //
    //     }, "B").start();
    // }
    
    /**
     * 获取占位符对应的值 按@x@中的x值获取(推荐)
     *
     * @param sourceJson Json源
     * @param modelJson  Json模板
     * @return map
     */
    public static Map<String, String> getPlaceholderValueMap(String sourceJson, String modelJson) {
        sourceJson = sourceJson.replaceAll("\\s+", "");
        modelJson = modelJson.replaceAll("\\s+", "");
        // System.out.println(sourceJson);
        // System.out.println(modelJson);
        
        String regex = "@\\d+@";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(modelJson);
        
        Map<String, String> modelStringMap = new HashMap<>();
        
        Map<String, Integer> modelPositionMap = new HashMap<>();
        
        int count = 0;
        while (matcher.find()) {
            // 记录占位符@x@的位置  @1@在模版中的第4位, @2@在模版中的第1位
            modelPositionMap.put(matcher.group(), count++);
        }
        
        String[] jsonSplit = modelJson.split(regex);
        
        try {
            for (int index = 0; index < count; index++) {
                String format = String.format("@%s@", index + 1);
                
                int position = modelPositionMap.get(format);
                
                int i = sourceJson.indexOf(jsonSplit[position]) + jsonSplit[position].length();
                int i1 = sourceJson.indexOf(jsonSplit[position + 1]);
                String substring = sourceJson.substring(i, i1);
                modelStringMap.put(format, substring);
            }
        } catch (Exception exception) {
            System.out.println(exception.toString());
        }
        return modelStringMap;
    }
    
    /**
     * 获取占位符对应的值 按@x@顺序获取
     *
     * @param sourceJson Json源
     * @param modelJson  Json模板
     * @return map
     */
    @Deprecated()
    public static Map<String, String> getPlaceholderValueMap2(String sourceJson, String modelJson) {
        sourceJson = sourceJson.replaceAll("\\s+", "");
        modelJson = modelJson.replaceAll("\\s+", "");
        
        String pattern = "@\\d+@";
        
        String[] jsonSplit = modelJson.split(pattern);
        
        Map<String, String> modelStringMap = new HashMap<>();
        
        for (int jsonSplitIndex = 0; jsonSplitIndex < jsonSplit.length; jsonSplitIndex++) {
            int jsonSplitIndexPlus = jsonSplitIndex + 1;
            if (jsonSplitIndexPlus >= jsonSplit.length) {
                break;
            }
            
            int i = sourceJson.indexOf(jsonSplit[jsonSplitIndex]) + jsonSplit[jsonSplitIndex].length();
            int i1 = sourceJson.indexOf(jsonSplit[jsonSplitIndexPlus]);
            String substring = sourceJson.substring(i, i1);
            modelStringMap.put(String.format("@%s@", jsonSplitIndexPlus), substring);
        }
        
        return modelStringMap;
    }
    
    
}