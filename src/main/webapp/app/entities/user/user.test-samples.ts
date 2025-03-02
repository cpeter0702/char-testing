import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 27087,
  login: 'H',
};

export const sampleWithPartialData: IUser = {
  id: 22733,
  login: '6BNl',
};

export const sampleWithFullData: IUser = {
  id: 6120,
  login: '&h?@HUY\\rlD1\\bxysR\\%6\\kh6ejse\\}P',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
