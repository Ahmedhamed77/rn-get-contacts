import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'rn-get-contacts' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const RnGetContacts = NativeModules.RnGetContacts
  ? NativeModules.RnGetContacts
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export interface Contact {
  id: string;
  name: string;
  numbers: string[];
  emails: string[];
}

export function getContacts(): Promise<Contact[]> {
  return RnGetContacts.getContacts();
}
