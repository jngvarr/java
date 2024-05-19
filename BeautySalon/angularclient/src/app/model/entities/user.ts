import {Someone} from "./someone";

export class User extends Someone {
  username: string | undefined;
  password: string | undefined;
  email: string | undefined;
}
