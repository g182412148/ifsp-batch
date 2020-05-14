package util;


import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;
import com.scrcu.ebank.ebap.batch.bean.mapper.BthBatchAccountFileMapper;
import org.apache.commons.io.input.XmlStreamReader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Test2019 {

    private static Map<String,Set<String>> merReadCardTypeMapping;
    private static Set<String> readCardTypes;

    static {
        readCardTypes = new HashSet<String>();
        merReadCardTypeMapping = new HashMap<String,Set<String>>();
        readCardTypes.add("03");
        readCardTypes.add("04");
        readCardTypes.add("93");
        readCardTypes.add("94");

        merReadCardTypeMapping.put("977",readCardTypes);

        readCardTypes = new HashSet<String>();
        readCardTypes.add("02");
        readCardTypes.add("05");
        merReadCardTypeMapping.put("988",readCardTypes);

    }

    public static void main(String[] args) {
       /* System.out.println("run test");
        System.out.println(11 / 100);
        System.out.println("processors count : " + Runtime.getRuntime().availableProcessors());*/

       /*for (int i = 0; i < 100; i++)
       {
           int pageCount = (int)Math.ceil((double) 20051 / 50);
           System.out.println("pageCount : " + pageCount);

       }*/

       //test2();
        test3("988");

    }

    public static void testSqlSession() throws  IOException
    {
        String source = "mybatis-config.xml";
        InputStream is = null;    //Resources
        is = Resources.getResourceAsStream(source);

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(new XmlStreamReader(is));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        sqlSession.selectList("selectByPrimaryKey");
        //blah blah ...
        sqlSession.getMapper(BthBatchAccountFileMapper.class).insertSelective(new BthBatchAccountFile());
    }

    public static void test1() throws ClassNotFoundException
    {
        System.out.println("test1");
        Class clazz = Class.forName("com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccDtl");

        try {
            clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void test2()
    {
        System.out.println("977====>>"+merReadCardTypeMapping.get("977"));
        System.out.println("988====>>"+merReadCardTypeMapping.get("988"));
    }


    public static boolean test3(String mernoStartWith)
    {
        Set<String> readCardTypes;
        switch (mernoStartWith)
        {
            case "952" :
                System.out.println("952");
                return false;
            case "966":
                System.out.println("966");
                return false;
            default:
                readCardTypes = merReadCardTypeMapping.get(mernoStartWith);

        }
        System.out.println("set : " + readCardTypes);
        return true;
    }

    @Test
    public void test(){
//        testThread testThread = new testThread(6);
//        Thread thread =new Thread(testThread);
//        thread.start();
//        System.out.println("qqqqq");


    }
}
