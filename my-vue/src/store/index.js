import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
    const token = ref(localStorage.getItem('token') || '')

    const setUserInfo = (info) => {
        userInfo.value = info
        localStorage.setItem('userInfo', JSON.stringify(info))
    }

    const setToken = (newToken) => {
        token.value = newToken
        localStorage.setItem('token', newToken)
    }

    const logout = () => {
        userInfo.value = {}
        token.value = ''
        localStorage.removeItem('userInfo')
        localStorage.removeItem('token')
    }

    const isLoggedIn = () => {
        return !!token.value
    }

    return {
        userInfo,
        token,
        setUserInfo,
        setToken,
        logout,
        isLoggedIn
    }
})