# FlashGet Multi-thread Downloader
### Description
This is Application that can download files from URL.This apllication will use multiple threads to download faster.

### Features
![Main](https://cdn.discordapp.com/attachments/689474874762461249/710033292857966653/unknown.png)

* **URL TextField**  - For enter the URL
* **Download Button**  - For click on this button to download
* **Clear Button**  - For click on this button to clear download

### Usage
Verify that your Jar file is runnable by runnig ot your self.

Then use this command:
```
java -jar FlashGet.jar
```
For Java 11 you nedd to specify the module path for JavaFx.

Use this command:
```
java --module-path \lib\javafx-sdk-11.0.2\lib --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar FlashGet.jar
```


 **1**.Enter your URL in to text field
 ![Start](https://cdn.discordapp.com/attachments/632960013631225893/710034602340777994/unknown.png)
 
 **2**.Click the Download button then it will show you Directory to save file.
 ![Chooser](https://cdn.discordapp.com/attachments/632960013631225893/710034719609323525/unknown.png)
 
 **3**.After choose,The application will start download untill finished.
 ![Download](https://cdn.discordapp.com/attachments/632960013631225893/710038008639782932/unknown.png)
 
 **4**.After finished program will alert you.
 ![Alert](https://cdn.discordapp.com/attachments/632960013631225893/710038107642265600/unknown.png)
 
 ### Other Features
 
 **Clear Button**
 Clear Button for clear when you finished your lastest download and want new download.
 ![Clear](https://cdn.discordapp.com/attachments/632960013631225893/710038241620918272/unknown.png)
 
 **Cancel Button**
 Cancel Button for cancel your donwload.
 ![Cancel](https://cdn.discordapp.com/attachments/632960013631225893/710038577333141564/unknown.png)
 
 ### UML Digram
 ![UML](https://cdn.discordapp.com/attachments/632960013631225893/710043720799617064/Package_FlashGet.png)