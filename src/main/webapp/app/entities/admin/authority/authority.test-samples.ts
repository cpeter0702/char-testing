import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '42295cce-be16-4475-ac02-7a33322dd586',
};

export const sampleWithPartialData: IAuthority = {
  name: 'd8c80ac7-8a6e-4f97-833c-b98c93978e55',
};

export const sampleWithFullData: IAuthority = {
  name: '3fdf9845-0db8-4d00-afc2-7d52b9a96323',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
