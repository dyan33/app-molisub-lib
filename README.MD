# build.gradle

```angular2
repositories {

    maven {
        url "https://raw.githubusercontent.com/dyan33/app-molisub-lib/master/repos/releases"

        credentials {
            username "git账号"
            password "git密码"
        }

        authentication {
            basic(BasicAuthentication)
        }
    }

}

dependencies {
   implementation 'com.enhtmv:sublib:版本号'

}
```