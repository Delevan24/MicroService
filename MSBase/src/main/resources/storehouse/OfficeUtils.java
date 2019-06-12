import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.HtmlOfficeMathOutputMode;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/*
 * @Author daiwei
 * @Description
 * @Date 2018/11/8 14:44
 **/
public class OfficeUtils {
	
	public static void main(String[] args) {
		String outPath = "e:\\ppt.pdf";
		
		//		word2pdf(inPath, outPath);
		word2pdf("e:\\cjm-温州嘉一接口需求文档.docx",outPath,null);
	}
    static {
        try {
            InputStream wordsLicense = OfficeUtils.class.getClassLoader().getResourceAsStream("storehouse/aspose.words.license.xml");// 凭证文件
            InputStream slidesLicense =OfficeUtils.class.getClassLoader().getResourceAsStream("storehouse/aspose.slides.license.xml");// 凭证文件
            InputStream cellsLicense = OfficeUtils.class.getClassLoader().getResourceAsStream("storehouse/aspose.cells.license.xml");// 凭证文件

            com.aspose.slides.License slidesLic = new com.aspose.slides.License();
            slidesLic.setLicense(slidesLicense);
            com.aspose.words.License wordsLic = new com.aspose.words.License();
            wordsLic.setLicense(wordsLicense);
            com.aspose.cells.License cellsLic = new com.aspose.cells.License();
            cellsLic.setLicense(cellsLicense);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * word转pdf
     * @param inPath
     * @param outPath
     */
    public static boolean word2pdf(String inPath, String outPath,com.aspose.words.PdfSaveOptions options) {
        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath+".tmp"); // 新建一个空白pdf文档
            FileOutputStream os = new FileOutputStream(file);
            Document doc = new Document(inPath); // Address是将要被转化的word文档
            if(options==null) {
                options = new com.aspose.words.PdfSaveOptions();
            }
            doc.save(os, options);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
            os.close();
            // EPUB, XPS, SWF 相互转换
            Files.move(file, new File(outPath));
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * html文件转word文件
     * @param inPath
     * @param outPath
     * @param options
     * @return
     */
    public static boolean html2word(String inPath, String outPath,com.aspose.words.HtmlLoadOptions options){
        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath+".tmp"); // 新建一个空白pdf文档
            FileOutputStream os = new FileOutputStream(file);
            if(options==null) {
                options = new com.aspose.words.HtmlLoadOptions();
                options.setEncoding(Charset.forName("utf-8"));
                options.setLoadFormat(com.aspose.words.LoadFormat.HTML);

            }
            Document doc = new Document(inPath,options); // Address是将要被转化的word文档
            doc.save(os,com.aspose.words.SaveFormat.DOC);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
            os.close();
            // EPUB, XPS, SWF 相互转换
            Files.move(file, new File(outPath));
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * html文本转换成word文档
     * @param html
     * @param outPath
     * @return
     */
    public static boolean htmlText2Word(String html,String outPath,com.aspose.words.HtmlLoadOptions options){
        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath+".tmp"); // 新建一个空白pdf文档
            FileOutputStream os = new FileOutputStream(file);
            if(options==null) {
                options = new com.aspose.words.HtmlLoadOptions();
                options.setEncoding(Charset.forName("utf-8"));
                options.setLoadFormat(com.aspose.words.LoadFormat.HTML);

            }
            Document doc = new Document(file.getAbsolutePath(),options); // Address是将要被转化的word文档
            DocumentBuilder builder = new DocumentBuilder(doc);
            builder.insertHtml(html);
            doc.save(os,com.aspose.words.SaveFormat.DOC);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
            os.close();
            // EPUB, XPS, SWF 相互转换
            Files.move(file, new File(outPath));
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * word转html
     * @param inPath
     * @param outPath
     */
    public static boolean word2html(String inPath, String outPath,com.aspose.words.HtmlSaveOptions options) {
        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath+".tmp"); // 新建一个空白pdf文档
            FileOutputStream os = new FileOutputStream(file);
            Document doc = new Document(inPath); // Address是将要被转化的word文档
            if(options==null) {
                options = new com.aspose.words.HtmlSaveOptions();
                //options.setResourceFolder("静态资源绝对路径");
                options.setOfficeMathOutputMode(HtmlOfficeMathOutputMode.IMAGE);
                options.setExportImagesAsBase64(true);
                options.setExportOriginalUrlForLinkedImages(false);
            }
            doc.save(os, options);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
            os.close();
            // EPUB, XPS, SWF 相互转换
            Files.move(file, new File(outPath));
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ppt转pdf
     * @param inPath
     * @param outPath
     */
    public static boolean ppt2pdf(String inPath, String outPath) {
        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath+".tmp"); // 新建一个空白pdf文档
            FileOutputStream os = new FileOutputStream(file);
            Presentation doc = new Presentation(inPath); // Address是将要被转化的word文档
            doc.save(os, com.aspose.slides.SaveFormat.Pdf);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
            os.close();
            // EPUB, XPS, SWF 相互转换
            Files.move(file, new File(outPath));
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ppt转html
     * @param inPath
     * @param outPath
     * @return
     */
    public static boolean ppt2html(String inPath, String outPath) {
        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath+".tmp"); // 新建一个空白pdf文档
            FileOutputStream os = new FileOutputStream(file);
            Presentation doc = new Presentation(inPath); // Address是将要被转化的word文档
            doc.save(os, com.aspose.slides.SaveFormat.Html);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
            os.close();
            // EPUB, XPS, SWF 相互转换
            Files.move(file, new File(outPath));
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * excel转pdf
     * @param inPath
     * @param outPath
     */
    public static boolean excel2pdf(String inPath, String outPath,com.aspose.cells.PdfSaveOptions options) {
        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath+".tmp"); // 新建一个空白pdf文档
            FileOutputStream os = new FileOutputStream(file);
            // load spreadsheet containing the chart
            com.aspose.cells.Workbook book = new com.aspose.cells.Workbook(inPath);

            if(options==null) {
                options = new com.aspose.cells.PdfSaveOptions();
                options.setOnePagePerSheet(true);
                options.setAllColumnsInOnePagePerSheet(true);
            }

            // get the chart present in first worksheet
            //com.aspose.cells.Chart chart = book.getWorksheets().get(0).getCharts().get(0);
            book.save(os, options);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
            os.close();
            // EPUB, XPS, SWF 相互转换
            Files.move(file, new File(outPath));
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * excel转html
     * @param inPath
     * @param outPath
     */
    public static boolean excel2html(String inPath, String outPath,com.aspose.cells.HtmlSaveOptions options) {
        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath+".tmp"); // 新建一个空白pdf文档
            FileOutputStream os = new FileOutputStream(file);
            // load spreadsheet containing the chart
            com.aspose.cells.Workbook book = new com.aspose.cells.Workbook(inPath);

            if(options==null) {
                options = new com.aspose.cells.HtmlSaveOptions();
                options.setExportImagesAsBase64(true);
                options.setExportActiveWorksheetOnly(true);
            }

            // get the chart present in first worksheet
            //com.aspose.cells.Chart chart = book.getWorksheets().get(0).getCharts().get(0);
            book.save(os, options);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
            os.close();
            // EPUB, XPS, SWF 相互转换
            Files.move(file, new File(outPath));
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 通用excel导入
     * @param path
     * @return
     */
    // public static List<Map<String,Object>> commonExcelImport(String path){
    //     return commonExcelImport(path,null);
    // }

    /**
     * 通用excel导入
     * @param path
     * @return
     */
   /* public static List<Map<String,Object>> commonExcelImport(String path,ImportParams params){
        List<Map<String,Object>> list=new ArrayList<>();
        if(params==null) {
            params = new ImportParams();
        }
        FileInputStream is=null;
        try {
            is=new FileInputStream(new File(path));
            list = ExcelImportUtil.importExcel(is, Map.class, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return list;
    }*/

    /**
     * 通用导入，返回某实体对象列表
     * 对于有图片的列，图片左上角坐标需要位于所属单元格内
     * @param path
     * @param params
     * @param clazz 自定义类，
     *             图片解析默认路径"/excel/upload/img"，程序需拥有读写权限
     *             普通类注解： @Excel(name = "a")
     *             图片字段注解： @Excel(name = "b", type = 2 ,imageType = 1)
     * @return
     */
   /* public static List commonExcelImport(String path, ImportParams params,Class clazz){
        List list=new ArrayList<>();
        if(params==null) {
            params = new ImportParams();
        }
        FileInputStream is=null;
        try {
            is=new FileInputStream(new File(path));
            list = ExcelImportUtil.importExcel(is, clazz, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return list;
    }

    *//**
     * 使用html的table生成excel表格
     * table的sheetName属性为sheet名称，必须指定
     * @param html
     * @param outPath
     *//*
    public static boolean html2Excel(String html, String outPath){
        try {
            Workbook workbook = ExcelXorHtmlUtil.htmlToExcel(html.toString(), ExcelType.XSSF);
            File savefile = new File(outPath+".tmp");
            FileOutputStream fos = new FileOutputStream(savefile);
            workbook.write(fos);
            fos.close();
            Files.move(savefile, new File(outPath));
            return true;
        }catch (Exception ex){
            System.out.println(ex.toString());
        }
        return false;
    }

    *//**
     * 使用html的url的table生成excel表格
     * table的sheetName属性为sheet名称，必须指定
     * @param url url地址
     * @param charset 编码
     * @param outPath 输出文件
     *//*
    public static boolean htmlUrl2Excel(String url, String charset,String outPath){
        try {
            String html= HttpClient
                    .textBody(url)
                    .charset(StringUtils.isEmpty(charset)?"utf-8":charset)
                    .text(null).execute().asString();

            Workbook workbook = ExcelXorHtmlUtil.htmlToExcel(html, ExcelType.XSSF);
            File savefile = new File(outPath+".tmp");
            FileOutputStream fos = new FileOutputStream(savefile);
            workbook.write(fos);
            fos.close();
            Files.move(savefile, new File(outPath));
            return true;
        }catch (Exception ex){
            System.out.println(ex.toString());
        }
        return false;
    }

    *//**
     * latex转换成MathML
     * @param content
     * @return
     *//*
    public static String convertLatex2MathML(String content){
        long old = System.currentTimeMillis();
        String mathML = fmath.conversion.ConvertFromLatexToMathML.convertToMathML(content);
        mathML = mathML.replaceFirst("<math ", "<math xmlns=\"http://www.w3.org/1998/Math/MathML\" ");
        long now = System.currentTimeMillis();
        System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
        return mathML;
    }

    *//**
     * latex转换成base64图片格式
     * @param content
     * @return
     *//*
    public static String convertLatex2Base64Image(String content){
        try {
            long old = System.currentTimeMillis();
            TeXFormula formula = new TeXFormula(content);
            // render the formla to an icon of the same size as the formula.
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
            // insert a border
            icon.setInsets(new Insets(5, 5, 5, 5));
            // now create an actual image of the rendered equation
            BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2 = image.createGraphics();
            g2.setColor(Color.white);
            g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
            JLabel jl = new JLabel();
            jl.setForeground(new Color(0, 0, 0));
            icon.paintIcon(jl, g2, 0, 0);
            // at this point the image is created, you could also save it with ImageIO
            // saveImage(image, "png", "F:\\b.png");
            // ImageIO.write(image, "png", new File("F:\\c.png"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "png", outputStream);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            byte[] buffer = outputStream.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
            return ("data:image/png;base64," + encoder.encode(buffer));
        } catch (Exception e) {
            // e.printStackTrace();
            // ExceptionUtil.log(log, e);
            System.err.println("公式解析有误：\n" + content);
            // e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        //OfficeUtils.word2html("/Users/david/Documents/公式.docx","/Users/david/Documents/1w.html",null);
        //OfficeUtils.word2pdf("/Users/david/Documents/E1-L5-M1-06非洲音乐-LP.docx","/Users/david/Documents/1w.pdf",null);
        //OfficeUtils.ppt2pdf("/Users/david/Documents/1.pptx","/Users/david/Documents/1p.pdf");
        //OfficeUtils.ppt2html("/Users/david/Documents/1.pptx","/Users/david/Documents/1p.html");
        //OfficeUtils.excel2pdf("/Users/david/Documents/1.xlsx","/Users/david/Documents/1.pdf",null);

        //OfficeUtils.html2word("/Users/david/Documents/1w.html","/Users/david/Documents/1w.doc",null);
        //OfficeUtils.htmlText2Word("<table sheetName='test' border=1><tr><td colspan=2>中文t</td></tr><tr><td>1</td><td>2</td></tr></table>","/Users/david/Documents/1w.doc",null);
        ImportParams params=new ImportParams();
        //params.setNeedSave(true);
        //params.setSaveUrl("/Users/david/Documents/gitdata/base/upload");
        //OfficeUtils.commonExcelImport("/Users/david/Documents/1111tupian.xlsx",params,ImageImportTestBean.class);
        //System.out.println(OfficeUtils.convertLatex2MathML("\\(x = {-b \\pm \\sqrt{b^2-4ac} \\over 2a}\\)"));
        //OfficeUtils.html2Excel("<html><table sheetName='test'><tr><td colspan=2>t</td></tr><tr><td>1</td><td>2</td></tr></table></html>","/Users/david/Documents/1ex.xlsx");
        //OfficeUtils.htmlUrl2Excel("http://www.baidu.com",null,"/Users/david/Documents/1ex.xlsx");
    }*/
}
