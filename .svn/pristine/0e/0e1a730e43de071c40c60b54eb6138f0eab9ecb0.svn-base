package kd.cus.wb.usethe;

import org.apache.commons.io.output.XmlStreamWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

public class xmlmain {

    public static void main(String[] args) {
        Person person = new Person("abc", "男", "北京", "朝阳区");

        File file = new File("E:\\person.xml");
        JAXBContext jc = null;
        try {
            //根据Person类生成上下文对象
            jc = JAXBContext.newInstance(Person.class);
            //从上下文中获取Marshaller对象，用作将bean编组(转换)为xml
            Marshaller ma = jc.createMarshaller();
            //以下是为生成xml做的一些配置
            //格式化输出，即按标签自动换行，否则就是一行输出
            ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //设置编码（默认编码就是utf-8）
            ma.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            //是否省略xml头信息，默认不省略（false）
            ma.setProperty(Marshaller.JAXB_FRAGMENT, true);

            //编组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ma.marshal(person,baos);
            String str1= baos.toString();
            System.out.println(1);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
