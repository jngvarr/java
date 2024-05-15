import {Someone} from "./someone";

export class User extends Someone {
  userName: string | undefined;
  password: string | undefined;
  email: string | undefined;
}
