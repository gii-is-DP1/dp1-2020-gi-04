import React, { useEffect, useState } from "react";
import axios, { AxiosRequestConfig, AxiosResponse } from "axios";

export const useGet = (url: string, config?: AxiosRequestConfig) => {
  const { data, loading, setExecuteCall } = useFetch(() =>
    axios.get(url, config)
  );
  useEffect(() => {
    setExecuteCall();
  }, [url]);
  return { data, loading };
};

export const usePost = (url: string, config?: AxiosRequestConfig) => {
  const [payload, setPayload] = useState(null);
  const { data, loading, setExecuteCall } = useFetch(() =>
    axios.post(url, payload, config)
  );
  return {
    data,
    loading,
    post: (dataPayload: any) => {
      setPayload(dataPayload);
      setExecuteCall();
    },
  };
};

export const usePut = (
  url: string,
  data?: any,
  config?: AxiosRequestConfig
) => {
  return useFetch(() => axios.put(url, data, config));
};

export const useDelete = (url: string, config?: AxiosRequestConfig) => {
  return useFetch(() => axios.delete(url, config));
};

export const useFetch = (fetchCall: () => Promise<AxiosResponse>) => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<AxiosResponse | null>(null);
  const [executeCall, setExecuteCall] = useState(0);
  useEffect(() => {
    let isMounted = true;

    const fetchData = async () => {
      if (!executeCall) return;
      setLoading(() => true);
      const result = await fetchCall();
      if (isMounted) {
        setData(result);
        setLoading(() => false);
      }
    };
    fetchData();
    return () => {
      isMounted = false;
    };
  }, [executeCall]);

  return {
    loading,
    data,
    setExecuteCall: () => {
      if (!loading) {
        setExecuteCall(executeCall + 1);
      }
    },
  };
};
