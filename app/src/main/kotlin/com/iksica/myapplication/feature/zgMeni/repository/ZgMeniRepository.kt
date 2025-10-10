package com.iksica.myapplication.feature.zgMeni.repository

import com.iksica.myapplication.feature.zgMeni.services.ZgMeniService

class ZgMeniRepository(val zgMeniService: ZgMeniService) {

    fun fetchLocationData() = zgMeniService.fetchLocationData()
    fun fetchMenies() = zgMeniService.fetchMenies()

}