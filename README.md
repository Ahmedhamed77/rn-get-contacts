# rn-get-contacts

this is a native package to read contacts only

## Installation

```sh
npm install rn-get-contacts
```

or 

```sh
yarn add rn-get-contacts
```


## Usage


```js
import { getContacts } from 'rn-get-contacts';
import {
  Text,
  View,
  StyleSheet,
  Pressable,
  PermissionsAndroid,
  Platform,
} from 'react-native';
import { useEffect } from 'react';

export default function App() {
  async function fetchContacts() {
    try {
      const contacts = await getContacts();
      console.log('Contacts:', contacts);
    } catch (error) {
      console.error('Error fetching contacts:', error);
    }
  }

  const getAllContacts = async () => {
    if (Platform.OS === 'ios') {
      await fetchContacts();
    } else {
      PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.READ_CONTACTS, {
        title: 'Contacts',
        message: 'This app would like to view your contacts.',
        buttonPositive: 'Please accept bare mortal',
      })
        .then((res) => {
          console.log('Permission: ', res);
          fetchContacts();
        })
        .catch((error) => {
          console.error('Permission error: ', error);
        });
    }
  };

  useEffect(() => {
    getAllContacts();
  }, []);

  return (
    <View style={styles.container}>
      <Text>weclome the useEffect will run to get contacts</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});


// ...



```


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
