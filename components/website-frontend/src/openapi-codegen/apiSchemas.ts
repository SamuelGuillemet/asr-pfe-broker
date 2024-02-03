/**
 * Generated by @openapi-codegen
 *
 * @version 0.3.0
 */
/**
 * This this the account model that is linked to the database and used by the API.
 *
 * Args:
 *     AccountBase: The base model to use.
 */
export type Account = {
  stocks?: Stock[];
  balance?: number | null;
  /**
   * @maxLength 32
   * @minLength 3
   */
  username: string;
  lastName: string;
  firstName: string;
  id: number;
  scope: SecurityScopes;
  enabled: boolean;
};

export type AccountCreate = {
  /**
   * @maxLength 32
   * @minLength 3
   */
  username: string;
  lastName: string;
  firstName: string;
  password: string;
};

export type AccountUpdate = {
  username?: string | null;
  lastName?: string | null;
  firstName?: string | null;
  password?: string | null;
  scope?: SecurityScopes | null;
  enabled?: boolean | null;
};

export type Balance = {
  balance?: number | null;
};

export type BalanceUpdate = {
  /**
   * @minimum 0
   */
  balance: number;
};

export type BodyLogin = {
  grant_type?: string | null;
  username: string;
  password: string;
  /**
   * @default
   */
  scope?: string;
  client_id?: string | null;
  client_secret?: string | null;
};

export type HTTPError = {
  /**
   * Error message.
   */
  detail: string;
};

export type HTTPValidationError = {
  detail?: ValidationError[];
};

export type HealthResponse = {
  /**
   * OK
   */
  status: string;
};

export type OwnAccountUpdate = {
  username?: string | null;
  lastName?: string | null;
  firstName?: string | null;
  password?: string | null;
};

export type RootResponse = {
  /**
   * Hello, World!
   */
  msg: string;
};

export type SecurityScopes = 'user' | 'admin';

export type Stock = {
  symbol: string;
  quantity: number;
};

export type StockUpdate = {
  /**
   * @minimum 0
   */
  quantity: number;
};

export type Token = {
  access_token: string;
  /**
   * @default bearer
   */
  token_type?: string;
};

export type ValidationError = {
  loc: (string | number)[];
  msg: string;
  type: string;
};

export type VersionResponse = {
  /**
   * Version of the API.
   */
  version: string;
};
