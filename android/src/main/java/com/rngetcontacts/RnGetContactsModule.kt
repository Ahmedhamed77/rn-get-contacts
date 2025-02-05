package com.rngetcontacts

import android.content.pm.PackageManager
import android.provider.ContactsContract
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


data class Contact(val id: String, val name: String) {
  var numbers = ArrayList<String>()
  var emails = ArrayList<String>()
}

class RnGetContactsModule(private val reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {



  private val scope = CoroutineScope(Dispatchers.IO)


  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun getContacts(promise: Promise) {
    scope.launch {
      try {
        val contactsListAsync = async { getPhoneContacts() }
        val contactNumbersAsync = async { getContactNumbers() }
        val contactEmailAsync = async { getContactEmails() }

        val contacts = contactsListAsync.await()
        val contactNumbers = contactNumbersAsync.await()
        val contactEmails = contactEmailAsync.await()

        contacts.forEach { contact ->
          contactNumbers[contact.id]?.let { numbers ->
            contact.numbers = numbers
          }
          contactEmails[contact.id]?.let { emails ->
            contact.emails = emails
          }
        }

        val result = Arguments.createArray()

        contacts.forEach { contact ->
          val contactMap = Arguments.createMap()
          contactMap.putString("id", contact.id)
          contactMap.putString("name", contact.name)

          val numbersArray = Arguments.createArray()
          contact.numbers.forEach { number ->
            numbersArray.pushString(number)
          }
          contactMap.putArray("numbers", numbersArray)

          val emailsArray = Arguments.createArray()
          contact.emails.forEach { email ->
            emailsArray.pushString(email)
          }
          contactMap.putArray("emails", emailsArray)

          result.pushMap(contactMap)
        }

        promise.resolve(result)
      } catch (e: Exception) {
        promise.reject("ERROR", e.message)
      }
    }
  }


  private fun getPhoneContacts(): ArrayList<Contact> {
    val contactList = ArrayList<Contact>()
    val contactsCursor = reactContext.contentResolver?.query(
      ContactsContract.Contacts.CONTENT_URI,
      null,
      null,
      null,
      ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )

    if (contactsCursor !== null && contactsCursor.count > 0) {
      val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
      val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

      while (contactsCursor.moveToNext()) {
        val id = contactsCursor.getString(idIndex)
        val name = contactsCursor.getString(nameIndex)
        if (name !== null) {
          contactList.add(Contact(id, name))
        }
      }
      contactsCursor.close()
    }

    return contactList
  }

  private fun getContactNumbers(): HashMap<String, ArrayList<String>> {
    val contactsNumberMap = HashMap<String, ArrayList<String>>()
    val phoneCursor = reactContext.contentResolver.query(
      ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
      null,
      null,
      null,
      null
    )
    if (phoneCursor !== null && phoneCursor.count > 0) {
      val contactIdIndex =
        phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
      val numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
      while (phoneCursor.moveToNext()) {
        val contactId = phoneCursor.getString(contactIdIndex)
        val number = phoneCursor.getString(numberIndex)
        if (contactsNumberMap.containsKey(contactId)) {
          contactsNumberMap[contactId]?.add(number)
        } else {
          contactsNumberMap[contactId] = arrayListOf(number)
        }
      }
      phoneCursor.close()
    }
    return contactsNumberMap
  }


  private fun getContactEmails():HashMap<String,ArrayList<String>> {
    val contactsEmailMap = HashMap<String,ArrayList<String>>()
    val emailCursor = reactContext.contentResolver.query(
      ContactsContract.CommonDataKinds.Email.CONTENT_URI,
      null,
      null,
      null,
      null
    )

    if(emailCursor!==null && emailCursor.count>0 ) {
      val contactIdIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
      val emailIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)

      while(emailCursor.moveToNext()) {
        val contactId = emailCursor.getString(contactIdIndex)
        val email = emailCursor.getString(emailIndex)
        if(contactsEmailMap.containsKey(contactId)) {
          contactsEmailMap[contactId]?.add(email)
        }
        else {
          contactsEmailMap[contactId] = arrayListOf(email)
        }
      }
      emailCursor.close()
    }

    return contactsEmailMap
  }

  
  override fun invalidate() {
    super.invalidate()
    scope.cancel()
  }


  companion object {
    const val NAME = "RnGetContacts"
  }
}
