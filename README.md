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
  ActivityIndicator,
} from 'react-native';
import { useEffect, useState } from 'react';

export default function App() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function fetchContacts() {
    try {
      await getContacts();
    } catch (err: any) {
      const errorMessage = err.message || 'Unknown error occurred';
      setError(errorMessage);
      console.error('Error fetching contacts:', err);
    }
  }

  const getAllContacts = async () => {
    setLoading(true);
    setError(null);

    if (Platform.OS === 'ios') {
      await fetchContacts();
    } else {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.READ_CONTACTS,
        {
          title: 'Contacts Permission',
          message: 'This app needs access to your contacts.',
          buttonPositive: 'Allow',
          buttonNegative: 'Deny',
        }
      );

      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        await fetchContacts();
      } else {
        setError('Contacts permission denied');
      }
    }

    setLoading(false);
  };

  useEffect(() => {
    getAllContacts();
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Welcome</Text>

      {loading ? (
        <ActivityIndicator size="large" color="#0000ff" />
      ) : error ? (
        <Text style={styles.errorText}>{error}</Text>
      ) : (
        <Text style={styles.successText}>Contacts fetched successfully!</Text>
      )}

      <Pressable style={styles.button} onPress={getAllContacts}>
        <Text style={styles.buttonText}>Fetch Contacts</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  successText: {
    fontSize: 16,
    marginVertical: 20,
    textAlign: 'center',
    color: 'green',
  },
  errorText: {
    fontSize: 16,
    marginVertical: 20,
    textAlign: 'center',
    color: 'red',
  },
  button: {
    backgroundColor: '#007BFF',
    paddingVertical: 12,
    paddingHorizontal: 30,
    borderRadius: 8,
    marginTop: 20,
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
});


```


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
