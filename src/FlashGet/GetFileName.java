package FlashGet;

/*
* For get File name from url
* @author Jakkrathorn Srisawad
* */
public class GetFileName {

    /*
    * For get File name from url
    * @param String url
    * @return fileName
    * */
    public String get(String url) {
        String[] getLink = url.split("/");
        String fileName = getLink[getLink.length - 1];
        return fileName;
    }

}
