import axios from 'axios'
import type {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  InternalAxiosRequestConfig
} from 'axios'
import { ElMessage, ElLoading } from 'element-plus'

interface RequestConfig extends AxiosRequestConfig {
  loading?: boolean
}

export interface ApiResponse<T = any> {
  code: number
  data: T
  msg: string
}

const defaultConfig: AxiosRequestConfig = {
  baseURL: '/api',
  timeout: 60000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    Accept: 'application/json, text/plain, */*'
  },
  withCredentials: true,
  timeoutErrorMessage: '请求超时',
  responseEncoding: 'utf-8',
  validateStatus: (status: number) => status >= 200 && status < 300
}

class Request {
  private readonly instance: AxiosInstance
  private loadingInstance: any = null

  constructor() {
    this.instance = axios.create(defaultConfig)
    this.setupInterceptors()
  }

  private setupInterceptors() {
    this.instance.interceptors.request.use(
      (config: InternalAxiosRequestConfig & { loading?: boolean }) => {
        if (config.loading) {
          this.showLoading()
        }
        return config
      },
      (error) => {
        this.hideLoading()
        ElMessage.error('请求失败')
        return Promise.reject(error)
      }
    )

    this.instance.interceptors.response.use(
      (response: AxiosResponse<ApiResponse>) => {
        this.hideLoading()
        const res = response.data
        if (res.code === 200) {
          return res.data
        }
        ElMessage.error(res.msg || '请求失败')
        return Promise.reject(res)
      },
      (error) => {
        this.hideLoading()
        let msg = '网络异常'
        if (!error.response) msg = '服务器连接失败'
        else {
          const status = error.response.status
          switch (status) {
            case 401:
              msg = '登录已过期'
              break
            case 403:
              msg = '权限不足'
              break
            case 404:
              msg = '接口不存在'
              break
            case 500:
              msg = '服务器错误'
              break
          }
        }
        if (error.message.includes('timeout')) msg = '请求超时'
        ElMessage.error(msg)
        return Promise.reject(error)
      }
    )
  }

  private showLoading() {
    this.loadingInstance = ElLoading.service({
      lock: true,
      text: '加载中...',
      background: 'rgba(0,0,0,0.05)'
    })
  }

  private hideLoading() {
    this.loadingInstance?.close()
  }

  get<T = any>(url: string, config?: RequestConfig): Promise<T> {
    return this.instance.get(url, config)
  }

  post<T = any>(url: string, data?: any, config?: RequestConfig): Promise<T> {
    return this.instance.post(url, data, config)
  }
}

export default new Request()
