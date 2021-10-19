package com.example.flickrbrowserapp

class Photo {

    var title = ""
    var server = ""
    var id = ""
    var secret = ""

    fun url(size: String = ""): String{
        var url = ""
        if(size.isNotEmpty()){
            url = "https://live.staticflickr.com/$server/${id}_${secret}_$size.jpg"
        }else{
            url = "https://live.staticflickr.com/$server/${id}_$secret.jpg"
        }
        return url
    }
}