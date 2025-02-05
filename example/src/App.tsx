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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <View style={styles.container}>
      <Pressable onPress={fetchContacts}>
        <Text>Press Me</Text>
      </Pressable>
      <Text>Result: 12</Text>
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
