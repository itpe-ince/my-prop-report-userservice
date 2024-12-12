import dayjs from 'dayjs';
import { GenderType } from 'app/shared/model/enumerations/gender-type.model';

export interface IUserInfo {
  id?: number;
  userId?: string;
  firstname?: string;
  lastname?: string;
  alias?: string;
  gender?: keyof typeof GenderType;
  email?: string;
  phone?: string | null;
  addressLine1?: string | null;
  addressLine2?: string | null;
  city?: string | null;
  country?: string | null;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IUserInfo> = {};
