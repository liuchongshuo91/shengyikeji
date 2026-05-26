import request from './request'

export interface DictItem {
  id: string
  no: string
  name: string
  parentId?: string
  type?: string
  leaf: boolean
}

export const api = {
  dict: () => request.get<Record<string, DictItem[]>>('/dict'),
  page: (data: any) => request.post<any>('/reimbursements/page', data),
  detail: (id: string) => request.get<any>(`/reimbursements/${id}`),
  save: (data: any) => request.post<any>('/reimbursements/save', data),
  submit: (data: any) => request.post<any>('/reimbursements/submit', data),
  voidDocument: (id: string) => request.post<any>(`/reimbursements/${id}/void`),
  deleteReimbursement: (id: string) => request.post<any>(`/reimbursements/${id}/delete`)
}
