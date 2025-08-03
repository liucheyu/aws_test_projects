import type { RuntimeConfig } from "nuxt/schema";
import type { FetchContext } from "ofetch";

type HttpHeaders = Record<string, string>;
type QueryParams = Record<string, string | number | boolean>;
type HttpBody = Record<string, any>;

type HttpOptions = {
    headers?: HttpHeaders;
    baseURL?: string;
    server?: boolean;
    lazy?: boolean;
    immediate?: boolean;
};

class HttpService {
  get = async (url: string, query?: QueryParams, options?: HttpOptions) => {
    return await useFetch(url, {
      method: "GET",
      query: query,
      headers: options?.headers,
      baseURL: options?.baseURL,
      lazy: options?.lazy,
      immediate: options?.immediate,
    });
  };

  post = async (url: string, data: HttpBody, options?: HttpOptions) => {
    return await useFetch(url, {
      method: "POST",
      body: data,
      headers: options?.headers,
      baseURL: options?.baseURL,
      lazy: options?.lazy,
      immediate: options?.immediate,
    });
  };

  put = async (url: string, data: HttpBody, options?: HttpOptions) => {
    return await useFetch(url, {
      method: "PUT",
      body: data,
      headers: options?.headers,
      baseURL: options?.baseURL,
      lazy: options?.lazy,
      immediate: options?.immediate,
    });
  };

  delete = async (url: string, query?: QueryParams, options?: HttpOptions) => {
    return await useFetch(url, {
      method: "DELETE",
      query: query,
      headers: options?.headers,
      baseURL: options?.baseURL,
      lazy: options?.lazy,
      immediate: options?.immediate,
    });
  };

  onRequest = ({ request, options }: FetchContext) => {
    // Set the request headers
    //console.log(request, options);
  };
  onRequestError = ({ request, options, error }: FetchContext) => {
    // Handle the request errors
    //console.log(request, options, error)
  };

  onResponse = ({ request, response, options }: FetchContext) => {
    // Process the response data
  };

  onResponseError = ({ request, response, options }: FetchContext) => {
    // Handle the response errors
    //console.log(request, options, error)
  };
}

class HttpAdapter {
  http: HttpService;

  constructor() {
    this.http = new HttpService();
  }

  setPath = (path: string) => {
    // currentPath為閉包，避免多個地方調用，互相影響
    const currentPath = path;
    return {
      get: async (queryParam?: QueryParams, options?: HttpOptions) => {
        return this.http.get(currentPath, queryParam, options);
      },

      post: async (body: HttpBody, options?: HttpOptions) => {
        return this.http.post(currentPath, body, options);
      },
      put: async (body: HttpBody, options?: HttpOptions) => {
        return this.http.put(currentPath, body, options);
      },
      delete: async (queryParam?: QueryParams, options?: HttpOptions) => {
        return this.http.delete(currentPath, queryParam, options);
      },
    };
  };
}


class RestApi {
  httpAdapter: HttpAdapter;
  headers = {};
  options?: HttpOptions;
  runtimeConfig: RuntimeConfig

  constructor(runtimeConfig: RuntimeConfig) {
    this.httpAdapter = new HttpAdapter();
    this.runtimeConfig = runtimeConfig;    
  }

  path = (path: string) => {
    return this.httpAdapter.setPath(path);
  };

  // TODO: 也可定義好類似定義以下的function
  frontendLogin = () => {
      return this.httpAdapter.setPath(`${this.runtimeConfig.public.frontendHost}/login`);
  };
}

export const useRestApi = () => {
  const runtimeConfig = useRuntimeConfig()
  return new RestApi(runtimeConfig)
};
