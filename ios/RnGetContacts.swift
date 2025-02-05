import Contacts
import ContactsUI

@objc(RnGetContacts)
class RnGetContacts: NSObject {


  @objc(getContacts:rejecter:)
  func getContacts(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
    let store = CNContactStore()
    let keys = [
      CNContactIdentifierKey,
      CNContactGivenNameKey,
      CNContactFamilyNameKey,
      CNContactPhoneNumbersKey,
      CNContactEmailAddressesKey
    ] as [CNKeyDescriptor]
    
    let request = CNContactFetchRequest(keysToFetch: keys)
    
    do {
      var contacts: [[String: Any]] = []
      
      try store.enumerateContacts(with: request) { contact, _ in
        var contactDict: [String: Any] = [:]
        
        contactDict["id"] = contact.identifier
        
        // Name (combine given and family name)
        let fullName = [contact.givenName, contact.familyName]
          .filter { !$0.isEmpty }
          .joined(separator: " ")
        contactDict["name"] = fullName
        
        // Phone Numbers
        var numbers: [String] = []
        for number in contact.phoneNumbers {
          numbers.append(number.value.stringValue)
        }
        contactDict["numbers"] = numbers
        
        // Email Addresses
        var emails: [String] = []
        for email in contact.emailAddresses {
          emails.append(email.value as String)
        }
        contactDict["emails"] = emails
        
        contacts.append(contactDict)
      }
      
      resolve(contacts)
      
    } catch {
      reject("ERROR", "Failed to fetch contacts: \(error.localizedDescription)", error)
    }
  }
}
