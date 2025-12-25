import {Contact} from "~/playground/data/common";
import type {BodyPart} from "~/playground/data/geometry";
import type {RoomModel} from "~/playground/data/model";
import {ContactUtils, IntersectionsPolygonsContactDetector} from "~/playground/utils/contact-utils";
import {Trapeze} from "~/playground/data/geometry";
import type {SurfaceState} from "~/playground/data/state";

export const SurfaceContactUtils = {

  getContacts(bodyPart: BodyPart, roomModel: RoomModel, withMaxDepth: boolean): Set<Contact> {
    const surfaces = roomModel.state.surfaces
    const contacts = new Set<Contact>()
    if (!surfaces || surfaces.length === 0) {
      return contacts
    }
    const maxDepth = withMaxDepth ? roomModel.specs.surfaceMaxDepth : 0.0
    surfaces.forEach(surface => {
      const contact = this.getContact(bodyPart, surface, maxDepth)
      if (contact) {
        contacts.add(contact)
      }
    })
    return contacts
  },

  getContact(bodyPart: BodyPart, surface: SurfaceState, maxDepth: number): Contact | null {
    const trapeze = Trapeze.ofSurface(surface)
    const surfaceBoundaries = trapeze.getBoundaries()
    const bodyBoundaries = bodyPart.getBoundaries()
    if (surfaceBoundaries.noOverlap(bodyBoundaries)) {
      return null
    }
    const contact = ContactUtils.getBodyPartsContactWithDetector(bodyPart, trapeze,
        new IntersectionsPolygonsContactDetector())
    if (contact) {
      if (maxDepth > 0) {
        return Contact.withNormal(
          contact.depth - maxDepth,
          contact.normal,
          contact.position
        )
      }
      return contact
    }
    return null
  }
}
