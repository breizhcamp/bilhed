import type {PassType} from "@/dto/Group";

export interface PersonFilter {
  status?: string
  lastname?: string
  firstname?: string
  email?: string
  pass?: PassType
  drawn?: boolean
  payed?: boolean
}