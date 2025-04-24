# ToneTagHelloUpi

**In MainActivity.Java, please replace the below mentioned variables with your's.**

    private String BIC = "YOUR_BIC"; //Request to Tonetag Team at allocate the BIC
    private String SUB_KEY_STAGING = "YOUR_STAGING_SUBSCRIPTION_KEY"; //Request to Tonetag Team at allocate the staging sub key
    private String SUB_KEY_PRODUCTION = "YOUR_PRODUCTION_SUBSCRIPTION_KEY"; //Request to Tonetag Team at allocate the production sub key


# Steps to integrate the library
Minimum SDK version is 23 and library compiled with api level 34.

**1. Define permissions in app’s manifest file within application tag**
<uses-permission android:name="android.permission.INTERNET" /> 
<uses-permission android:name="android.permission.RECORD_AUDIO" /> 
<uses-permission android:name="android.permission.READ_CONTACTS" /> 

**2. From the Project view pane, locate the build.gradle file under the **app** folder and open it for editing.**

In build.gradle, add the following into the dependencies { ... } section: 

a. implementation 'com.squareup.okhttp3:okhttp:4.11.0' 
b. implementation 'org.tensorflow:tensorflow-lite-task-audio:0.4.4' 
c. implementation 'org.bitbucket.team_android_apps:helloupi:1.0.0'
d. implementation 'com.github.bumptech.glide:glide:4.16.0' 
e. annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0' 

**3. From the Project view pane, locate the settings.gradle and please add the maven url as specified below**

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

# How to request activation keys
We’re excited for you to explore the integration showcased in this repository! To get started with a trial, simply drop us a request at <a href="mailto:dev&#64;xyz&#46;com">Contact Us</a> with the following details:

- Company Name  
- Contact Name  
- Company Email  
- Phone Number
  
Once we receive your request, we’ll promptly share your trial activation keys so you can dive right in. Looking forward to having you onboard and hearing your feedback!
