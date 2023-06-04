package com.alsadimoh.graduationproject.retrofit.models

data class ResponseClassWithoutItems(var status:Boolean, var message:String, var pages: PagesModel, var errors:List<ResponseErrorsModel> )
