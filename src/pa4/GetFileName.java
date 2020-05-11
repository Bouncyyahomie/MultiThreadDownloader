package pa4;

/*
* For get File name from url
* @author Jakkrathorn Srisawad
* */
public class GetFileName {
    String url;

    public void Getfilename(String url){
        this.url = url;
    }

    public String get() {
        String[] getLink = this.url.split("/");
        String fileName = getLink[getLink.length - 1];
        return fileName;
    }

}
